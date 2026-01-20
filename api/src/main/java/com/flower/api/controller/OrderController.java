package com.flower.api.controller;

import com.flower.cart.dto.CartDto;
import com.flower.cart.dto.CartItemDto;
import com.flower.cart.dto.CartItemOptionDto;
import com.flower.cart.service.CartService;
import com.flower.common.exception.EntityNotFoundException;
import com.flower.order.dto.CreateDirectOrderRequest;
import com.flower.order.dto.CreateOrderRequest;
import com.flower.order.dto.CreateOrderResponse;
import com.flower.order.dto.OrderDetailDto;
import com.flower.order.dto.OrderDto;
import com.flower.order.dto.OrderItemDto;
import com.flower.order.dto.OrderItemOptionDto;
import com.flower.order.dto.UpdateOrderStatusRequest;
import com.flower.order.service.OrderService;
import com.flower.product.dto.ProductDto;
import com.flower.product.dto.ProductOptionDto;
import com.flower.product.service.ProductQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "다이렉트 상품 주문", description = "장바구니를 거치지 않고 바로 상품을 주문합니다.")
    @PostMapping("/direct")
    public ResponseEntity<CreateOrderResponse> createDirectOrder(@RequestBody CreateDirectOrderRequest request) {
        log.info("Received direct order request for member: {}, product: {}", request.memberId(), request.productId());

        // 1. 상품 정보 조회
        ProductDto product = productQueryService.getProductById(request.productId());
        
        // 2. 주문 상품(OrderItem) 생성
        List<OrderItemDto> orderItems = new ArrayList<>();
        
        List<OrderItemOptionDto> options = new ArrayList<>();
        if (request.optionIds() != null && !request.optionIds().isEmpty()) {
            List<ProductOptionDto> productOptions = productQueryService.getOptionsByIds(request.optionIds());
            for (ProductOptionDto opt : productOptions) {
                 String optionName = opt.name() + ": " + opt.optionValue();
                 options.add(OrderItemOptionDto.builder()
                     .productOptionId(opt.id())
                     .optionName(optionName)
                     .price(opt.priceAdjustment())
                     .build());
            }
        }

        orderItems.add(OrderItemDto.builder()
                .productId(product.id())
                .productName(product.name())
                .quantity(request.quantity())
                .unitPrice(product.price())
                .options(options)
                .build());

        // 3. CreateOrderRequest 변환 (공통 주문 로직 재사용)
        CreateOrderRequest orderRequest = new CreateOrderRequest(
                request.memberId(),
                request.deliveryMethod(),
                request.reservedAt(),
                request.messageCard(),
                request.deliveryAddress(),
                request.deliveryPhone(),
                request.deliveryName(),
                request.deliveryNote(),
                true
        );

        // 4. 주문 생성
        CreateOrderResponse response = orderService.createOrder(orderRequest, orderItems);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 주문 목록 조회", description = "회원의 주문 내역을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<List<OrderDto>> getMyOrders(@RequestParam Long memberId) {
        return ResponseEntity.ok(orderService.getOrdersByMemberId(memberId));
    }

    @Operation(summary = "전체 주문 조회 (관리자)", description = "모든 회원의 주문 내역을 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @Operation(summary = "주문 상태 변경 (관리자)", description = "주문의 상태를 변경합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request));
    }

    @Operation(summary = "주문 상세 조회", description = "주문 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailDto> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderId));
    }

    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrderById(orderId);
        return ResponseEntity.ok().build();
    }
}
