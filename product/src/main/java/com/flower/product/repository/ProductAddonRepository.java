package com.flower.product.repository;

import com.flower.product.domain.ProductAddon;
import com.flower.product.domain.ProductAddon.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 추가 상품 엔티티를 위한 리포지토리 인터페이스
 */
@Repository
public interface ProductAddonRepository extends JpaRepository<ProductAddon, Long> {

    /**
     * 카테고리별 추가 상품 조회
     */
    List<ProductAddon> findByCategory(Category category);

    /**
     * 사용 가능한 모든 추가 상품 조회
     */
    List<ProductAddon> findByIsAvailableOrderByDisplayOrderAsc(boolean isAvailable);

    /**
     * 카테고리 및 사용 가능 여부로 추가 상품 조회
     */
    List<ProductAddon> findByCategoryAndIsAvailableOrderByDisplayOrderAsc(Category category, boolean isAvailable);

    /**
     * 모든 추가 상품 조회 (사용 가능한 추가 상품 목록 조회용)
     */
    List<ProductAddon> findAllByOrderByDisplayOrderAsc();
}
