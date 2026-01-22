package com.flower.review.service;

import com.flower.common.exception.BusinessException;
import com.flower.common.exception.EntityNotFoundException;
import com.flower.order.service.OrderModuleService;
import com.flower.product.repository.ProductRepository;
import com.flower.review.domain.Review;
import com.flower.review.dto.CreateReviewRequest;
import com.flower.review.dto.ReviewDto;
import com.flower.review.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderModuleService orderModuleService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReview_success() {
        // given
        Long productId = 1L;
        Long memberId = 100L;
        Long orderItemId = 500L;
        CreateReviewRequest request = new CreateReviewRequest(productId, memberId, orderItemId, 5, "Great flower!");

        given(productRepository.existsById(productId)).willReturn(true);
        willDoNothing().given(orderModuleService).validateOrderItemForReview(orderItemId, memberId, productId);
        given(reviewRepository.existsByOrderItemId(orderItemId)).willReturn(false);
        given(reviewRepository.save(any(Review.class))).willAnswer(invocation -> {
            Review review = invocation.getArgument(0);
            return review; 
        });

        // when
        ReviewDto result = reviewService.createReview(request);

        // then
        assertThat(result.productId()).isEqualTo(productId);
        assertThat(result.memberId()).isEqualTo(memberId);
        assertThat(result.rating()).isEqualTo(5);
        verify(eventPublisher).publishEvent(any());
        verify(orderModuleService).validateOrderItemForReview(orderItemId, memberId, productId);
    }

    @Test
    @DisplayName("배송 완료 전 리뷰 작성 시 예외")
    void createReview_notDelivered() {
        // given
        Long productId = 1L;
        Long orderItemId = 500L;
        CreateReviewRequest request = new CreateReviewRequest(productId, 100L, orderItemId, 5, "Bad");

        given(productRepository.existsById(productId)).willReturn(true);
        willThrow(new BusinessException("배송이 완료된 상품에 대해서만 리뷰를 작성할 수 있습니다."))
            .given(orderModuleService).validateOrderItemForReview(orderItemId, 100L, productId);

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("배송이 완료된 상품");
    }

    @Test
    @DisplayName("주문 취소 시 리뷰 숨김 처리")
    void hideReviewsForOrderItems() {
        // given
        Long orderItemId = 100L;
        Review review = Review.builder()
                .productId(1L)
                .memberId(1L)
                .orderItemId(orderItemId)
                .rating(5)
                .content("Good")
                .build();
        
        // 초기 상태 확인
        assertThat(review.getIsHidden()).isFalse();

        given(reviewRepository.findByOrderItemIdIn(List.of(orderItemId)))
                .willReturn(List.of(review));

        // when
        reviewService.hideReviewsForOrderItems(List.of(orderItemId));

        // then
        assertThat(review.getIsHidden()).isTrue();
    }
}
