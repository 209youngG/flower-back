package com.flower.order.service;

import com.flower.common.event.OrderPlacedEvent;
import com.flower.common.event.OrderCancelledEvent;
import com.flower.order.domain.Order;
import com.flower.order.domain.OrderItem;
import com.flower.order.domain.OrderItemOption;
import com.flower.order.dto.CreateOrderRequest;
import com.flower.order.dto.CreateOrderResponse;
import com.flower.order.dto.OrderDetailDto;
import com.flower.order.dto.OrderDto;
import com.flower.order.dto.OrderItemDto;
import com.flower.order.dto.OrderItemOptionDto;
import com.flower.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.flower.common.exception.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void markAsPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        order.markAsPaid();
        order.setStatus(com.flower.order.domain.OrderStatus.PAID); // 결제 완료 시 상태 변경
        orderRepository.save(order);
    }

    @Transactional
    public void markAsRefunded(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        order.setPaymentStatus(com.flower.order.domain.Order.PaymentStatus.REFUNDED);
        orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(String orderNumber, String reason) {
        log.info("주문 취소 요청 - 주문번호: {}, 사유: {}", orderNumber, reason);
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다: " + orderNumber));
        
        order.cancel();
        log.info("주문 취소 완료 - 주문번호: {}", orderNumber);
    }

    @Transactional
    public void cancelOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        
        if (order.getStatus() == com.flower.order.domain.OrderStatus.SHIPPED || 
            order.getStatus() == com.flower.order.domain.OrderStatus.DELIVERED) {
            throw new IllegalStateException("이미 배송된 주문은 취소할 수 없습니다.");
        }
        
        order.cancel();
        
        List<OrderPlacedEvent.OrderItemInfo> items = order.getItems().stream()
                .map(item -> new OrderPlacedEvent.OrderItemInfo(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .collect(Collectors.toList());
        
        eventPublisher.publishEvent(new OrderCancelledEvent(
                order.getOrderNumber(),
                order.getId(), // orderId 주입
                "사용자 취소",
                order.getMemberId(),
                items
        ));
    }

    @Transactional(readOnly = true)
    public OrderDetailDto getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        
        List<OrderItemDto> itemDtos = order.getItems().stream()
            .map(item -> OrderItemDto.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build())
            .collect(Collectors.toList());

        return new OrderDetailDto(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getStatus().getDescription(),
                order.getCreatedAt(),
                order.getDeliveryName(),
                order.getDeliveryPhone(),
                order.getDeliveryAddress(),
                order.getDeliveryNote(),
                itemDtos
        );
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByMemberId(Long memberId) {
        return orderRepository.findByMemberIdOrderByCreatedAtDesc(memberId).stream()
                .map(order -> {
                    String itemSummary = order.getItems().isEmpty() ? "" :
                            order.getItems().get(0).getProductName() + 
                            (order.getItems().size() > 1 ? " 외 " + (order.getItems().size() - 1) + "건" : "");
                            
                    return new OrderDto(
                            order.getId(),
                            order.getOrderNumber(),
                            order.getTotalAmount(),
                            order.getStatus().name(),
                            order.getStatus().getDescription(),
                            order.getCreatedAt(),
                            itemSummary
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, List<OrderItemDto> orderItems) {
        log.info("주문 생성 요청 - 회원 ID: {}", request.memberId());

        // 1. 주문 엔티티 생성
        Order order = Order.builder()
                .memberId(request.memberId())
                .deliveryMethod(request.deliveryMethod())
                .reservedAt(request.reservedAt())
                .messageCard(request.messageCard())
                .deliveryAddress(request.deliveryAddress())
                .deliveryPhone(request.deliveryPhone())
                .deliveryName(request.deliveryName())
                .deliveryNote(request.deliveryNote())
                .build();

        // 2. 주문 상품 추가
        for (OrderItemDto itemDto : orderItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(itemDto.getProductId())
                    .productName(itemDto.getProductName())
                    .quantity(itemDto.getQuantity())
                    .unitPrice(itemDto.getUnitPrice())
                    .build();

            if (itemDto.getOptions() != null) {
                for (OrderItemOptionDto optionDto : itemDto.getOptions()) {
                    OrderItemOption option = OrderItemOption.builder()
                            .orderItem(orderItem)
                            .productOptionId(optionDto.getProductOptionId())
                            .productAddonId(optionDto.getProductAddonId())
                            .optionName(optionDto.getOptionName())
                            .priceAdjustment(optionDto.getPrice())
                            .build();
                    orderItem.addOption(option);
                }
            }
            
            // 총 가격은 엔티티 저장 시(@PrePersist) 또는 getTotalPrice() 호출 시 자동 계산됨
            order.addItem(orderItem);
        }

        // 3. 총 금액 계산
        order.calculateTotal();

        // 4. 저장
        Order savedOrder = orderRepository.save(order);

        // 5. 이벤트 데이터 준비
        String itemSummary = savedOrder.getItems().stream()
                .map(item -> item.getProductName() + " x " + item.getQuantity())
                .collect(Collectors.joining(", "));
        
        int totalQuantity = savedOrder.getItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        List<OrderPlacedEvent.OrderItemInfo> itemInfos = savedOrder.getItems().stream()
                .map(item -> new OrderPlacedEvent.OrderItemInfo(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .collect(Collectors.toList());

        OrderPlacedEvent.DeliveryInfo deliveryInfo = new OrderPlacedEvent.DeliveryInfo(
                savedOrder.getDeliveryName(),
                savedOrder.getDeliveryPhone(),
                savedOrder.getDeliveryAddress(),
                savedOrder.getDeliveryNote()
        );

        // 6. 이벤트 발행
        eventPublisher.publishEvent(new OrderPlacedEvent(
                savedOrder.getOrderNumber(), // orderNumber (Business Key)
                savedOrder.getId(),          // orderId (Internal ID)
                itemSummary,
                totalQuantity,
                savedOrder.getTotalAmount(),
                itemInfos,
                deliveryInfo
        ));
        
        log.info("주문 생성 완료 - 주문번호: {}", savedOrder.getOrderNumber());

        return CreateOrderResponse.from(savedOrder);
    }
}
