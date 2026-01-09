package com.flower.api.controller;

import com.flower.cart.domain.Cart;
import com.flower.cart.domain.CartItem;
import com.flower.cart.domain.CartItemOption;
import com.flower.cart.service.CartService;
import com.flower.common.exception.EntityNotFoundException;
import com.flower.order.domain.Order;
import com.flower.order.dto.CreateOrderRequest;
import com.flower.order.dto.OrderItemDto;
import com.flower.order.dto.OrderItemOptionDto;
import com.flower.order.service.OrderService;
import com.flower.product.domain.Product;
import com.flower.product.domain.ProductAddon;
import com.flower.product.domain.ProductOption;
import com.flower.product.service.ProductQueryService;
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
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final ProductQueryService productQueryService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        log.info("Received order request for member: {}", request.getMemberId());

        // 1. 장바구니 조회
        Cart cart = cartService.getCartByMemberId(request.getMemberId());
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // 2. 주문 데이터 준비
        // 상품명을 얻기 위해 상품 상세 정보 조회 (스냅샷)
        List<Long> productIds = cart.getItems().stream()
                .map(CartItem::getProductId)
                .collect(Collectors.toList());
        
        Map<Long, Product> productMap = productQueryService.getMapByIds(productIds);

        List<OrderItemDto> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = productMap.get(cartItem.getProductId());
            if (product == null) {
                throw new EntityNotFoundException("Product not found: " + cartItem.getProductId());
            }

            List<OrderItemOptionDto> optionDtos = new ArrayList<>();
            for (CartItemOption option : cartItem.getOptions()) {
                // 이상적으로는 여기서 옵션/추가상품 이름을 조회해야 함.
                // MVP의 경우 ProductService를 통해 조회하거나 상품 탐색에 의존할 수 있음.
                // ProductOption/Addon 엔티티가 Product 모듈에 있으므로 필요한 경우 ProductService를 통해 접근 가능.
                // 하지만 CartItemOption은 ID만 가지고 있음.
                
                String optionName = "Option #" + (option.getProductOptionId() != null ? option.getProductOptionId() : option.getProductAddonId());
                BigDecimal price = option.getPriceAdjustment();
                
                // TODO: ProductQueryService를 개선하여 ID로 옵션/추가상품 이름을 조회하도록 수정
                // 현재는 임시 이름 사용.
                
                optionDtos.add(OrderItemOptionDto.builder()
                        .productOptionId(option.getProductOptionId())
                        .productAddonId(option.getProductAddonId())
                        .optionName(optionName) 
                        .price(price)
                        .build());
            }

            orderItems.add(OrderItemDto.builder()
                    .productId(cartItem.getProductId())
                    .productName(product.getName())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .options(optionDtos)
                    .build());
        }

        // 3. 주문 생성
        Order order = orderService.createOrder(request, orderItems);

        // 4. 장바구니 비우기
        cartService.clearCart(cart.getCartKey());

        return ResponseEntity.ok(order);
    }
}
