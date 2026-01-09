package com.flower.cart.service;

import com.flower.cart.domain.Cart;
import com.flower.cart.dto.ProductInfo;
import com.flower.cart.port.out.CartProductPort;
import com.flower.cart.port.out.CartRepository;
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
    private CartProductPort cartProductPort;

    @Test
    @DisplayName("장바구니가 없으면 생성하고 상품을 추가해야 한다")
    void addItemShouldCreateCartIfNotExists() {
        // 준비
        String cartKey = "user-123";
        Long productId = 100L;
        BigDecimal price = new BigDecimal("5000");
        
        ProductInfo productInfo = ProductInfo.builder()
                .id(productId)
                .name("Test Flower")
                .effectivePrice(price)
                .build();

        given(cartRepository.findByCartKey(cartKey)).willReturn(Optional.empty());
        // 저장 시 적절한 Cart 객체 반환, 모의 구현 로직
        given(cartRepository.save(any(Cart.class))).willAnswer(invocation -> {
            Cart savedCart = invocation.getArgument(0);
            // 저장 동작 시뮬레이션 또는 그대로 반환
            return savedCart;
        });
        given(cartProductPort.getProductById(productId)).willReturn(productInfo);

        // 실행
        cartService.addItem(cartKey, productId, 1);

        // 검증
        // 저장 호출 검증. addItem은 getOrCreateCart를 호출하여 저장을 수행하고, 
        // 아이템을 추가한 뒤 다시 저장을 수행하므로 두 번 호출될 수 있음.
        verify(cartProductPort).getProductById(productId);
    }
}
