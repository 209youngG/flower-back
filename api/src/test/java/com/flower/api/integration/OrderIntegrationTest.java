package com.flower.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.cart.service.CartService;
import com.flower.order.domain.Order;
import com.flower.order.dto.CreateOrderRequest;
import com.flower.product.domain.Product;
import com.flower.product.domain.ProductCategory;
import com.flower.product.service.ProductService;
import com.flower.ApiApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {ApiApplication.class, TestConfig.class})
@Transactional
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class OrderIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("주문 전체 흐름 테스트: 장바구니 -> 주문 생성")
    void shouldCreateOrderFromCart() throws Exception {
        // 1. 데이터 준비
        Product product = Product.builder()
                .name("통합테스트 장미")
                .productCode("INT-001")
                .price(new BigDecimal("10000"))
                .discountPrice(null)
                .stockQuantity(100)
                .isActive(true)
                .isAvailableToday(true)
                .category(ProductCategory.FLOWER_GIFT)
                .deliveryType(com.flower.product.domain.Product.DeliveryType.PARCEL)
                .build();
        product = productService.createProduct(product);

        String cartKey = "cart-user-123";
        Long memberId = 1L;

        cartService.addItem(cartKey, product.getId(), 2);
        
        // Entity 직접 조작 대신 Service 메서드 사용
        cartService.assignMember(cartKey, memberId);

        // 2. 요청 준비 (Record 생성자 사용)
        CreateOrderRequest request = new CreateOrderRequest(
                memberId,
                Order.DeliveryMethod.SHIPPING,
                LocalDateTime.now().plusDays(3),
                "생일 축하합니다!",
                "서울 강남구",
                "010-1234-5678",
                "홍길동",
                null, 
                false 
        );

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").exists())
                .andExpect(jsonPath("$.totalAmount").value(20000.0))
                .andExpect(jsonPath("$.messageCard").value("생일 축하합니다!"));
    }
}
