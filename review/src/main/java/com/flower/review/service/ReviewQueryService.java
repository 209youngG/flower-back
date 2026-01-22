package com.flower.review.service;

import com.flower.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;

    /**
     * 주문 상품 ID 목록에 대해 리뷰 작성 여부를 확인하여 반환
     */
    public Map<Long, Boolean> getReviewStatusMap(List<Long> orderItemIds) {
        // 이미 작성된 리뷰의 orderItemId 목록 조회
        List<Long> reviewedItemIds = reviewRepository.findOrderItemIdsByOrderItemIdIn(orderItemIds);
        Set<Long> reviewedSet = Set.copyOf(reviewedItemIds);

        // Map으로 변환 (id -> true/false)
        return orderItemIds.stream()
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> reviewedSet.contains(id)
                ));
    }
}
