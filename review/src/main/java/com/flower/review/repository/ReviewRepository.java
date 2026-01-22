package com.flower.review.repository;

import com.flower.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.flower.review.dto.ProductRatingStatDto;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
    boolean existsByOrderItemId(Long orderItemId);

    @org.springframework.data.jpa.repository.Query("SELECT new com.flower.review.dto.ProductRatingStatDto(r.productId, COUNT(r), SUM(r.rating)) FROM Review r WHERE r.isHidden = false GROUP BY r.productId")
    List<ProductRatingStatDto> countReviewStatsByProduct();

    List<Review> findByOrderItemIdIn(List<Long> orderItemIds);
    
    @org.springframework.data.jpa.repository.Query("SELECT r.orderItemId FROM Review r WHERE r.orderItemId IN :orderItemIds AND r.isHidden = false")
    List<Long> findOrderItemIdsByOrderItemIdIn(@org.springframework.data.repository.query.Param("orderItemIds") List<Long> orderItemIds);
}
