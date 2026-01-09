package com.flower.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.cart.domain.Cart;
import com.flower.cart.port.out.CartRepository;
import com.flower.cart.service.CartService;
import com.flower.order.domain.Order;
import com.flower.order.dto.CreateOrderRequest;
import com.flower.product.domain.Product;
import com.flower.product.domain.ProductCategory;
import com.flower.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest(classes = TestConfig.class)
@Transactional
class OrderIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

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
                .discountPrice(null) // null은 할인 없음을 의미
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
        Cart cart = cartService.getCart(cartKey);
        cart.setMemberId(memberId);
        cartRepository.save(cart);

        // 2. 요청 준비
        CreateOrderRequest request = new CreateOrderRequest();
        request.setMemberId(memberId);
        request.setDeliveryMethod(Order.DeliveryMethod.SHIPPING);
        request.setReservedAt(LocalDateTime.now().plusDays(3));
        request.setMessageCard("생일 축하합니다!");
        request.setDeliveryAddress("서울 강남구");
        request.setDeliveryName("홍길동");
        request.setDeliveryPhone("010-1234-5678");

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").exists())
                .andExpect(jsonPath("$.totalAmount").value(20000.0))
                .andExpect(jsonPath("$.messageCard").value("생일 축하합니다!"));
    }
}
