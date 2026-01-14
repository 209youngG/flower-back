package com.flower.cart.service;

import com.flower.cart.domain.Cart;
import com.flower.cart.domain.CartItem;
import com.flower.cart.domain.CartItemOption;
import com.flower.cart.dto.CartDto;
import com.flower.cart.dto.CartItemDto;
import com.flower.cart.dto.CartItemOptionDto;
import com.flower.cart.dto.ProductInfo;
import com.flower.cart.port.out.CartProductPort;
import com.flower.cart.port.out.CartRepository;
import com.flower.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartProductPort cartProductPort;

    @Transactional
    public Cart getOrCreateCart(String cartKey) {
        return cartRepository.findByCartKey(cartKey)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .cartKey(cartKey)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    @Transactional(readOnly = true)
    public Cart getCart(String cartKey) {
        return cartRepository.findByCartKey(cartKey)
                .orElseThrow(() -> new EntityNotFoundException("해당 키의 장바구니를 찾을 수 없습니다: " + cartKey));
    }

    @Transactional
    public Cart getOrCreateCartByMemberId(Long memberId) {
        return cartRepository.findByMemberId(memberId)
                .orElseGet(() -> {
                    String cartKey = "cart-user-" + memberId;
                    Cart newCart = Cart.builder()
                            .cartKey(cartKey)
                            .memberId(memberId)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    @Transactional(readOnly = true)
    public Cart getCartByMemberId(Long memberId) {
        return cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원의 장바구니를 찾을 수 없습니다: " + memberId));
    }

    @Transactional
    public CartDto getCartDtoByMemberId(Long memberId) {
        Cart cart = getOrCreateCartByMemberId(memberId);
        return toDto(cart);
    }

    @Transactional
    public void assignMember(String cartKey, Long memberId) {
        Cart cart = getCart(cartKey);
        cart.setMemberId(memberId);
        cartRepository.save(cart);
    }

    @Transactional
    public CartItem addItem(String cartKey, Long productId, int quantity) {
        Cart cart = getOrCreateCart(cartKey);
        ProductInfo productInfo = cartProductPort.getProductById(productId);
        
        log.info("장바구니에 상품 추가: {} (수량: {}) - 카트: {}", productInfo.getName(), quantity, cartKey);

        CartItem item = cart.addItem(productInfo.getId(), productInfo.getEffectivePrice(), quantity);
        cartRepository.save(cart);
        return item;
    }

    @Transactional
    public void removeItem(String cartKey, Long itemId) {
        Cart cart = getCart(cartKey);
        cart.removeItem(itemId);
        cartRepository.save(cart);
        log.info("장바구니에서 아이템 삭제: {} - 카트: {}", itemId, cartKey);
    }

    @Transactional
    public void updateItemQuantity(String cartKey, Long itemId, int quantity) {
        Cart cart = getCart(cartKey);
        cart.updateItemQuantity(itemId, quantity);
        cartRepository.save(cart);
        log.info("장바구니 아이템 수량 변경: {} -> {} - 카트: {}", itemId, quantity, cartKey);
    }

    @Transactional
    public void clearCart(String cartKey) {
        Cart cart = getCart(cartKey);
        cart.clear();
        cartRepository.save(cart);
        log.info("장바구니 비우기 완료: {}", cartKey);
    }

    @Transactional
    public void deleteCart(String cartKey) {
        Cart cart = getCart(cartKey);
        cartRepository.delete(cart);
        log.info("장바구니 삭제 완료: {}", cartKey);
    }

    private CartDto toDto(Cart cart) {
        return new CartDto(
            cart.getCartKey(),
            cart.getMemberId(),
            cart.getItems().stream().map(this::toItemDto).collect(Collectors.toList()),
            cart.getTotalQuantity(),
            cart.getTotalPrice()
        );
    }

    private CartItemDto toItemDto(CartItem item) {
        return new CartItemDto(
            item.getId(),
            item.getProductId(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getOptions().stream().map(this::toOptionDto).collect(Collectors.toList()),
            item.getTotalPrice()
        );
    }

    private CartItemOptionDto toOptionDto(CartItemOption option) {
        return new CartItemOptionDto(
            option.getProductOptionId(),
            option.getProductAddonId(),
            option.getPriceAdjustment()
        );
    }
}
