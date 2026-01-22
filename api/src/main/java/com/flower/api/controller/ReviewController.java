package com.flower.api.controller;

import com.flower.review.dto.CreateReviewRequest;
import com.flower.review.dto.ReviewDto;
import com.flower.review.dto.UpdateReviewRequest;
import com.flower.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Review", description = "상품 리뷰 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "상품에 대한 리뷰를 작성합니다.")
    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody CreateReviewRequest request) {
        return ResponseEntity.ok(reviewService.createReview(request));
    }

    @Operation(summary = "상품별 리뷰 조회", description = "특정 상품의 리뷰 목록을 조회합니다.")
    @GetMapping("/products/{productId}")
    public ResponseEntity<List<ReviewDto>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    @Operation(summary = "리뷰 수정", description = "작성한 리뷰를 수정합니다.")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable Long reviewId,
            @RequestBody UpdateReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, request));
    }

    @Operation(summary = "리뷰 삭제", description = "작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long memberId) {
        reviewService.deleteReview(reviewId, memberId);
        return ResponseEntity.ok().build();
    }
}
