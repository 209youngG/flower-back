package com.flower.cart.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CartTest {

    @Test
    @DisplayName("장바구니에 상품을 추가하고 총액을 계산해야 한다")
    void shouldAddItemToCartAndCalculateTotals() {
        // 준비
        Cart cart = Cart.builder()
                .cartKey("test-cart-key")
                .build();

        Long productId = 1L;
        BigDecimal unitPrice = new BigDecimal("10000");
        int quantity = 2;

        // 실행
        cart.addItem(productId, unitPrice, quantity);

        // 검증
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getTotalQuantity()).isEqualTo(2);
        assertThat(cart.getTotalPrice()).isEqualByComparingTo(new BigDecimal("20000"));
    }

    @Test
    @DisplayName("이미 존재하는 상품이면 수량을 증가시켜야 한다")
    void shouldIncreaseQuantityIfItemAlreadyExists() {
        // 준비
        Cart cart = Cart.builder().cartKey("test-cart-key").build();
        Long productId = 1L;
        BigDecimal unitPrice = new BigDecimal("10000");

        cart.addItem(productId, unitPrice, 1);

        // 실행
        cart.addItem(productId, unitPrice, 2);

        // 검증
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(3);
        assertThat(cart.getTotalQuantity()).isEqualTo(3);
        assertThat(cart.getTotalPrice()).isEqualByComparingTo(new BigDecimal("30000"));
    }
}
