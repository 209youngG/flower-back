package com.flower.api.controller;

import com.flower.cart.dto.AddCartItemRequest;
import com.flower.cart.dto.CartDto;
import com.flower.cart.dto.UpdateCartItemRequest;
import com.flower.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "장바구니 관리 API")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 상품 담기", description = "장바구니에 상품을 추가합니다.")
    @PostMapping("/items")
    public ResponseEntity<Void> addItem(@RequestBody AddCartItemRequest request) {
        String cartKey = "cart-user-" + request.memberId(); // 임시 카트 키 생성 로직 (실제로는 세션/토큰 기반)
        cartService.addItem(cartKey, request.productId(), request.quantity(), request.optionIds());
        cartService.assignMember(cartKey, request.memberId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "장바구니 조회", description = "회원의 장바구니를 조회합니다.")
    @GetMapping
    public ResponseEntity<CartDto> getCart(@RequestParam Long memberId) {
        CartDto cart = cartService.getCartDtoByMemberId(memberId);
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "장바구니 상품 수량 수정", description = "장바구니에 담긴 상품의 수량을 변경합니다.")
    @PatchMapping("/items/{itemId}")
    public ResponseEntity<Void> updateItemQuantity(
            @RequestParam Long memberId,
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest request) {
        String cartKey = "cart-user-" + memberId;
        cartService.updateItemQuantity(cartKey, itemId, request.quantity());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "장바구니 상품 옵션 수정", description = "장바구니 아이템의 옵션을 변경합니다.")
    @PatchMapping("/items/{itemId}/options")
    public ResponseEntity<Void> updateItemOptions(
            @RequestParam Long memberId,
            @PathVariable Long itemId,
            @RequestBody com.flower.cart.dto.UpdateCartItemOptionRequest request) {
        String cartKey = "cart-user-" + memberId;
        cartService.updateItemOptions(cartKey, itemId, request.optionIds());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "장바구니 상품 삭제", description = "장바구니에서 특정 상품을 제거합니다.")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItem(
            @RequestParam Long memberId,
            @PathVariable Long itemId) {
        String cartKey = "cart-user-" + memberId;
        cartService.removeItem(cartKey, itemId);
        return ResponseEntity.ok().build();
    }
}
