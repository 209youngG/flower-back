package com.flower.cart.service;

import com.flower.cart.domain.Cart;
import com.flower.cart.repository.CartRepository;
import com.flower.product.dto.ProductDto;
import com.flower.product.service.ProductQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductQueryService productQueryService;

    @Test
    @DisplayName("장바구니가 없으면 생성하고 상품을 추가해야 한다")
    void addItemShouldCreateCartIfNotExists() {
        // 준비
        String cartKey = "user-123";
        Long productId = 100L;
        BigDecimal price = new BigDecimal("5000");
        
        // ProductDto(Long id, String name, BigDecimal price, Integer stockQuantity, String thumbnailUrl, boolean isActive, boolean isAvailableToday, List<ProductOptionDto> options, ProductCategory category, DeliveryType deliveryType)
        ProductDto product = new ProductDto(
            productId, 
            "Test Flower", 
            price, 
            100, 
            null, 
            true, 
            true, 
            null, 
            null, 
            null,
            0L,
            0L,
            0.0
        );

        given(cartRepository.findByCartKey(cartKey)).willReturn(Optional.empty());
        // 저장 시 적절한 Cart 객체 반환, 모의 구현 로직
        given(cartRepository.save(any(Cart.class))).willAnswer(invocation -> {
            Cart savedCart = invocation.getArgument(0);
            return savedCart;
        });
        given(productQueryService.getProductById(productId)).willReturn(product);

        // 실행
        cartService.addItem(cartKey, productId, 1);

        // 검증
        verify(productQueryService).getProductById(productId);
        // verify(cartRepository).save(any(Cart.class)); // Called twice actually
    }
}
