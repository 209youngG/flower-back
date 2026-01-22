package com.flower.order.service;

import com.flower.common.exception.BusinessException;
import com.flower.common.exception.EntityNotFoundException;
import com.flower.order.domain.OrderItem;
import com.flower.order.domain.OrderStatus;
import com.flower.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderModuleService {

    private final OrderRepository orderRepository;

    public void validateOrderItemForReview(Long orderItemId, Long memberId, Long productId) {
        OrderItem orderItem = orderRepository.findOrderItemById(orderItemId)
                .orElseThrow(() -> new EntityNotFoundException("주문 상품 정보를 찾을 수 없습니다: " + orderItemId));

        if (!orderItem.getProductId().equals(productId)) {
            throw new BusinessException("리뷰 작성하려는 상품과 주문 상품이 일치하지 않습니다.");
        }

        if (orderItem.getOrder().getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException("배송이 완료된 상품에 대해서만 리뷰를 작성할 수 있습니다.");
        }

        if (!orderItem.getOrder().getMemberId().equals(memberId)) {
            throw new BusinessException("본인이 주문한 상품에 대해서만 리뷰를 작성할 수 있습니다.");
        }
    }
}
