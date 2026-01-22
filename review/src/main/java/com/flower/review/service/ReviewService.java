package com.flower.review.service;

import com.flower.common.event.ReviewCreatedEvent;
import com.flower.common.exception.BusinessException;
import com.flower.common.exception.EntityNotFoundException;
import com.flower.common.exception.ErrorResponse;
import com.flower.order.service.OrderModuleService;
import com.flower.product.repository.ProductRepository;
import com.flower.review.domain.Review;
import com.flower.review.dto.CreateReviewRequest;
import com.flower.review.dto.ReviewDto;
import com.flower.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderModuleService orderModuleService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ReviewDto createReview(CreateReviewRequest request) {
        // 1. 상품 존재 검증
        if (!productRepository.existsById(request.productId())) {
            throw new EntityNotFoundException("Product not found: " + request.productId());
        }

        // 2. 구매 내역 및 배송 완료 여부 검증 (Order 모듈 위임)
        orderModuleService.validateOrderItemForReview(
                request.orderItemId(), 
                request.memberId(), 
                request.productId()
        );

        // 3. 중복 작성 검증 (1주문상품 1리뷰)
        if (reviewRepository.existsByOrderItemId(request.orderItemId())) {
            throw new BusinessException("이미 해당 주문 상품에 대한 리뷰를 작성했습니다.");
        }

        // 4. 리뷰 저장
        Review review = Review.builder()
                .productId(request.productId())
                .memberId(request.memberId())
                .orderItemId(request.orderItemId())
                .rating(request.rating())
                .content(request.content())
                .build();

        Review savedReview = reviewRepository.save(review);

        // 5. 이벤트 발행 (비동기 평점 갱신)
        eventPublisher.publishEvent(new ReviewCreatedEvent(
                this,
                savedReview.getId(),
                savedReview.getProductId(),
                savedReview.getRating()
        ));
        
        log.info("리뷰 작성 완료 및 이벤트 발행: ReviewId={}, ProductId={}", savedReview.getId(), savedReview.getProductId());

        return toDto(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .filter(review -> !review.getIsHidden()) // 숨겨진 리뷰 필터링
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void hideReviewsForOrderItems(List<Long> orderItemIds) {
        List<Review> reviews = reviewRepository.findByOrderItemIdIn(orderItemIds);
        for (Review review : reviews) {
            review.hide();
        }
        log.info("주문 취소로 인한 리뷰 숨김 처리 완료: {} 건", reviews.size());
    }

    private ReviewDto toDto(Review review) {
        return new ReviewDto(
            review.getId(),
            review.getProductId(),
            review.getMemberId(),
            review.getRating(),
            review.getContent(),
            review.getCreatedAt()
        );
    }
}
