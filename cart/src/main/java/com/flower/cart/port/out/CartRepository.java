package com.flower.cart.port.out;

import com.flower.cart.domain.Cart;
import java.util.Optional;

public interface CartRepository {
    Cart save(Cart cart);
    Optional<Cart> findByCartKey(String cartKey);
    Optional<Cart> findByMemberId(Long memberId);
    void delete(Cart cart);
}
