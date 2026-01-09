package com.flower.cart.adapter.out.persistence;

import com.flower.cart.domain.Cart;
import com.flower.cart.port.out.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CartPersistenceAdapter implements CartRepository {

    private final CartJpaRepository cartJpaRepository;

    @Override
    public Cart save(Cart cart) {
        return cartJpaRepository.save(cart);
    }

    @Override
    public Optional<Cart> findByCartKey(String cartKey) {
        return cartJpaRepository.findByCartKey(cartKey);
    }

    @Override
    public Optional<Cart> findByMemberId(Long memberId) {
        return cartJpaRepository.findByMemberId(memberId);
    }

    @Override
    public void delete(Cart cart) {
        cartJpaRepository.delete(cart);
    }
}
