package com.flower.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.api.security.UserPrincipal;
import com.flower.member.domain.Member;
import com.flower.member.domain.MemberRole;
import com.flower.product.domain.Product;
import com.flower.product.domain.ProductCategory;
import com.flower.product.dto.CreateProductRequest;
import com.flower.product.dto.ProductDto;
import com.flower.product.dto.UpdateProductRequest;
import com.flower.product.service.ProductQueryService;
import com.flower.product.service.ProductService;
import com.flower.store.dto.StoreDto;
import com.flower.store.domain.StoreStatus;
import com.flower.store.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SellerProductControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ProductService productService;

    @Mock
    private ProductQueryService productQueryService;

    @Mock
    private StoreService storeService;

    @InjectMocks
    private SellerProductController sellerProductController;

    private UserPrincipal sellerPrincipal;
    private StoreDto myStore;

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                .id(1L)
                .email("seller@test.com")
                .name("Seller")
                .role(MemberRole.SELLER)
                .build();
        sellerPrincipal = new UserPrincipal(member);

        myStore = new StoreDto(
                10L, 1L, "My Flower Shop", "Address", 37.5, 127.0,
                "010-1234-5678", "Description", null, null,
                Collections.emptyList(), StoreStatus.ACTIVE,
                LocalDateTime.now(), LocalDateTime.now()
        );

        mockMvc = MockMvcBuilders.standaloneSetup(sellerProductController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().equals(UserPrincipal.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return sellerPrincipal;
                    }
                })
                .build();
    }

    @Test
    @DisplayName("Should create store product when authenticated seller")
    void createStoreProduct_Success() throws Exception {
        CreateProductRequest request = new CreateProductRequest(
                "Rose Bouquet", "ROSE-001", "Beautiful roses",
                BigDecimal.valueOf(50000), 100, ProductCategory.FLOWER_BOUQUET,
                Product.DeliveryType.QUICK, "http://image.url", Collections.emptyList()
        );

        Product product = Product.builder().id(100L).storeId(10L).build();
        ProductDto responseDto = new ProductDto(
                100L, 10L, "Rose Bouquet", BigDecimal.valueOf(50000), BigDecimal.valueOf(50000),
                100, "Beautiful roses", "http://image.url", true, false,
                Collections.emptyList(), ProductCategory.FLOWER_BOUQUET, Product.DeliveryType.QUICK,
                0L, 0L, 0.0
        );

        given(storeService.getMyStore(anyLong())).willReturn(myStore);
        given(productService.createProduct(any(CreateProductRequest.class))).willReturn(product);
        given(productQueryService.getProductById(anyLong())).willReturn(responseDto);

        mockMvc.perform(post("/api/v1/seller/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.name").value("Rose Bouquet"));
    }

    @Test
    @DisplayName("Should get my store products when valid seller")
    void getMyStoreProducts_Success() throws Exception {
        ProductDto productDto = new ProductDto(
                100L, 10L, "Rose Bouquet", BigDecimal.valueOf(50000), BigDecimal.valueOf(50000),
                100, "Beautiful roses", "http://image.url", true, false,
                Collections.emptyList(), ProductCategory.FLOWER_BOUQUET, Product.DeliveryType.QUICK,
                0L, 0L, 0.0
        );

        given(storeService.getMyStore(anyLong())).willReturn(myStore);
        given(productService.getProductsByStoreId(anyLong())).willReturn(List.of(productDto));

        mockMvc.perform(get("/api/v1/seller/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(100L));
    }

    @Test
    @DisplayName("Should get my store product when owned by seller")
    void getMyStoreProduct_Success() throws Exception {
        Product product = Product.builder().id(100L).storeId(10L).build();
        ProductDto productDto = new ProductDto(
                100L, 10L, "Rose Bouquet", BigDecimal.valueOf(50000), BigDecimal.valueOf(50000),
                100, "Beautiful roses", "http://image.url", true, false,
                Collections.emptyList(), ProductCategory.FLOWER_BOUQUET, Product.DeliveryType.QUICK,
                0L, 0L, 0.0
        );

        given(storeService.getMyStore(anyLong())).willReturn(myStore);
        given(productService.getById(anyLong())).willReturn(product);
        given(productQueryService.getProductById(anyLong())).willReturn(productDto);

        mockMvc.perform(get("/api/v1/seller/products/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L));
    }

    @Test
    @DisplayName("Should update my store product when owned by seller")
    void updateMyStoreProduct_Success() throws Exception {
        UpdateProductRequest request = new UpdateProductRequest(
                "Updated Roses", "New description", BigDecimal.valueOf(55000), null,
                110, ProductCategory.FLOWER_BOUQUET, Product.DeliveryType.QUICK,
                true, true, "http://new.image.url", Collections.emptyList()
        );

        Product product = Product.builder().id(100L).storeId(10L).build();
        ProductDto responseDto = new ProductDto(
                100L, 10L, "Updated Roses", BigDecimal.valueOf(55000), BigDecimal.valueOf(55000),
                110, "New description", "http://new.image.url", true, true,
                Collections.emptyList(), ProductCategory.FLOWER_BOUQUET, Product.DeliveryType.QUICK,
                0L, 0L, 0.0
        );

        given(storeService.getMyStore(anyLong())).willReturn(myStore);
        given(productService.getById(anyLong())).willReturn(product);
        given(productQueryService.getProductById(anyLong())).willReturn(responseDto);

        mockMvc.perform(put("/api/v1/seller/products/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Roses"));
    }

    @Test
    @DisplayName("Should delete my store product when owned by seller")
    void deleteMyStoreProduct_Success() throws Exception {
        Product product = Product.builder().id(100L).storeId(10L).build();

        given(storeService.getMyStore(anyLong())).willReturn(myStore);
        given(productService.getById(anyLong())).willReturn(product);

        mockMvc.perform(delete("/api/v1/seller/products/100"))
                .andExpect(status().isOk());

        verify(productService).deleteProduct(100L);
    }

    @Test
    @DisplayName("Should return 400 when accessing other seller's product")
    void getMyStoreProduct_OtherSellerProduct_BadRequest() throws Exception {
        Product otherProduct = Product.builder().id(200L).storeId(20L).build(); // Other store

        given(storeService.getMyStore(anyLong())).willReturn(myStore);
        given(productService.getById(anyLong())).willReturn(otherProduct);

        mockMvc.perform(get("/api/v1/seller/products/200"))
                .andExpect(status().isBadRequest());
    }

    // Unauthorized/Forbidden tests are better suited for Security integration tests
    // or requires full security context which standaloneSetup doesn't provide.
    // I'll skip them here as they are trivial with @WithMockUser in a working @WebMvcTest.
}
