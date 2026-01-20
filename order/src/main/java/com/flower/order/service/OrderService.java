package com.flower.order.service;

import com.flower.common.event.OrderPlacedEvent;
import com.flower.common.event.OrderCancelledEvent;
import com.flower.order.domain.Order;
import com.flower.order.domain.OrderItem;
import com.flower.order.domain.OrderItemOption;
import com.flower.order.domain.OrderStatus;
import com.flower.order.dto.CreateOrderRequest;
import com.flower.order.dto.CreateOrderResponse;
import com.flower.order.dto.OrderDetailDto;
import com.flower.order.dto.OrderDto;
import com.flower.order.dto.OrderItemDto;
import com.flower.order.dto.OrderItemOptionDto;
import com.flower.order.dto.UpdateOrderStatusRequest;
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
        Order order = findOrderById(orderId);
        order.markAsPaid();
        orderRepository.save(order);
    }

    @Transactional
    public void markAsRefunded(Long orderId) {
        Order order = findOrderById(orderId);
        order.markAsRefunded();
        orderRepository.save(order);
    }

    @Transactional
    public void markAsFailed(Long orderId) {
        Order order = findOrderById(orderId);
        order.markAsFailed();
        orderRepository.save(order);

        eventPublisher.publishEvent(createOrderCancelledEvent(order, "결제 실패"));
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
        Order order = findOrderById(orderId);
        
        validateOrderCancellable(order.getStatus());
        
        order.cancel();
        
        eventPublisher.publishEvent(createOrderCancelledEvent(order, "사용자 취소"));
    }

    public OrderDetailDto getOrderDetail(Long orderId) {
        Order order = findOrderById(orderId);
        
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
                order.getMemberId(),
                order.getIsDirectOrder() != null && order.getIsDirectOrder(),
                itemDtos
        );
    }

    public List<OrderDto> getOrdersByMemberId(Long memberId) {
        return orderRepository.findByMemberIdWithItems(memberId).stream()
                .map(this::toOrderDto)
                .collect(Collectors.toList());
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAllWithItems().stream()
                .map(this::toOrderDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = findOrderById(orderId);
        
        order.setStatus(request.status());
        orderRepository.save(order);
        
        return toOrderDto(order);
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, List<OrderItemDto> orderItems) {
        log.info("주문 생성 요청 - 회원 ID: {}", request.memberId());

        Order order = Order.builder()
                .memberId(request.memberId())
                .deliveryMethod(request.deliveryMethod())
                .reservedAt(request.reservedAt())
                .messageCard(request.messageCard())
                .deliveryAddress(request.deliveryAddress())
                .deliveryPhone(request.deliveryPhone())
                .deliveryName(request.deliveryName())
                .deliveryNote(request.deliveryNote())
                .isDirectOrder(request.isDirectOrder() != null && request.isDirectOrder())
                .build();

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
            
            order.addItem(orderItem);
        }

        order.calculateTotal();

        Order savedOrder = orderRepository.save(order);

        publishOrderPlacedEvent(savedOrder);
        
        log.info("주문 생성 완료 - 주문번호: {}", savedOrder.getOrderNumber());

        return CreateOrderResponse.from(savedOrder);
    }

    // --- Private Helper Methods ---

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
    }

    private void validateOrderCancellable(OrderStatus status) {
        switch (status) {
            case SHIPPED, DELIVERED -> throw new IllegalStateException("이미 배송된 주문은 취소할 수 없습니다.");
            default -> {} // OK
        }
    }

    private String createItemSummary(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        String firstItemName = items.get(0).getProductName();
        int size = items.size();
        return size > 1 ? firstItemName + " 외 " + (size - 1) + "건" : firstItemName;
    }

    private OrderDto toOrderDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getStatus().getDescription(),
                order.getCreatedAt(),
                createItemSummary(order.getItems())
        );
    }

    private OrderCancelledEvent createOrderCancelledEvent(Order order, String reason) {
        List<OrderPlacedEvent.OrderItemInfo> items = order.getItems().stream()
                .map(item -> new OrderPlacedEvent.OrderItemInfo(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .collect(Collectors.toList());

        return new OrderCancelledEvent(
                order.getOrderNumber(),
                order.getId(),
                reason,
                order.getMemberId(),
                items
        );
    }

    private void publishOrderPlacedEvent(Order order) {
        String itemSummary = order.getItems().stream()
                .map(item -> item.getProductName() + " x " + item.getQuantity())
                .collect(Collectors.joining(", "));
        
        int totalQuantity = order.getItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        List<OrderPlacedEvent.OrderItemInfo> itemInfos = order.getItems().stream()
                .map(item -> new OrderPlacedEvent.OrderItemInfo(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .collect(Collectors.toList());

        OrderPlacedEvent.DeliveryInfo deliveryInfo = new OrderPlacedEvent.DeliveryInfo(
                order.getDeliveryName(),
                order.getDeliveryPhone(),
                order.getDeliveryAddress(),
                order.getDeliveryNote()
        );

        eventPublisher.publishEvent(new OrderPlacedEvent(
                order.getOrderNumber(),
                order.getId(),
                itemSummary,
                totalQuantity,
                order.getTotalAmount(),
                itemInfos,
                deliveryInfo,
                order.getIsDirectOrder()
        ));
    }
}
