package com.flower.order.service;

import com.flower.common.event.OrderPlacedEvent;
import com.flower.order.domain.Order;
import com.flower.order.domain.OrderItem;
import com.flower.order.domain.OrderItemOption;
import com.flower.order.dto.CreateOrderRequest;
import com.flower.order.dto.OrderItemDto;
import com.flower.order.dto.OrderItemOptionDto;
import com.flower.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Order createOrder(CreateOrderRequest request, List<OrderItemDto> items) {
        log.info("주문 생성 요청 - 회원 ID: {}", request.getMemberId());

        // 1. 주문 엔티티 생성
        Order order = Order.builder()
                .memberId(request.getMemberId())
                .deliveryMethod(request.getDeliveryMethod())
                .reservedAt(request.getReservedAt())
                .messageCard(request.getMessageCard())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryPhone(request.getDeliveryPhone())
                .deliveryName(request.getDeliveryName())
                .deliveryNote(request.getDeliveryNote())
                .build();

        // 2. 주문 상품 매핑
        for (OrderItemDto itemDto : items) {
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

        // 3. 주문 저장
        Order savedOrder = orderRepository.save(order);
        
        // 4. 이벤트 발행
        eventPublisher.publishEvent(new OrderPlacedEvent(
                savedOrder.getId().toString(), 
                "Order #" + savedOrder.getOrderNumber(), 
                savedOrder.getItems().size(), 
                savedOrder.getTotalAmount() != null ? savedOrder.getTotalAmount() : java.math.BigDecimal.ZERO
        ));
        
        log.info("주문 생성 완료: {}", savedOrder.getOrderNumber());
        return savedOrder;
    }
}
