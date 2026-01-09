package com.flower.api.controller;

import com.flower.cart.dto.CartDto;
import com.flower.cart.dto.CartItemDto;
import com.flower.cart.dto.CartItemOptionDto;
import com.flower.cart.service.CartService;
import com.flower.common.exception.EntityNotFoundException;
import com.flower.order.dto.CreateOrderRequest;
import com.flower.order.dto.CreateOrderResponse;
import com.flower.order.dto.OrderItemDto;
import com.flower.order.dto.OrderItemOptionDto;
import com.flower.order.service.OrderService;
import com.flower.product.dto.ProductDto;
import com.flower.product.service.ProductQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "주문 관리 API")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final ProductQueryService productQueryService;

    @Operation(summary = "주문 생성", description = "장바구니에 담긴 상품으로 주문을 생성합니다.")
    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        log.info("Received order request for member: {}", request.memberId());

        // 1. 장바구니 조회 (DTO 사용)
        CartDto cart = cartService.getCartDtoByMemberId(request.memberId());
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // 2. 주문 데이터 준비
        List<Long> productIds = cart.items().stream()
                .map(CartItemDto::productId)
                .collect(Collectors.toList());
        
        // 상품 정보 조회 (DTO 사용)
        Map<Long, ProductDto> productMap = productQueryService.getProductsMapByIds(productIds);

        List<OrderItemDto> orderItems = new ArrayList<>();

        for (CartItemDto cartItem : cart.items()) {
            ProductDto product = productMap.get(cartItem.productId());
            if (product == null) {
                throw new EntityNotFoundException("Product not found: " + cartItem.productId());
            }

            List<OrderItemOptionDto> optionDtos = new ArrayList<>();
            for (CartItemOptionDto option : cartItem.options()) {
                String optionName = "Option #" + (option.productOptionId() != null ? option.productOptionId() : option.productAddonId());
                BigDecimal price = option.priceAdjustment();
                
                optionDtos.add(OrderItemOptionDto.builder()
                        .productOptionId(option.productOptionId())
                        .productAddonId(option.productAddonId())
                        .optionName(optionName) 
                        .price(price)
                        .build());
            }

            orderItems.add(OrderItemDto.builder()
                    .productId(cartItem.productId())
                    .productName(product.name())
                    .quantity(cartItem.quantity())
                    .unitPrice(cartItem.unitPrice())
                    .options(optionDtos)
                    .build());
        }

        // 3. 주문 생성 (DTO 반환)
        CreateOrderResponse response = orderService.createOrder(request, orderItems);

        // 4. 장바구니 비우기
        cartService.clearCart(cart.cartKey());

        return ResponseEntity.ok(response);
    }
}
