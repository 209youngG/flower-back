package com.flower.product.repository;

import com.flower.product.domain.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 상품 옵션 엔티티를 위한 리포지토리 인터페이스
 */
@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    /**
     * 상품 ID로 옵션 조회
     */
    List<ProductOption> findByProductId(Long productId);

    /**
     * 상품 ID와 사용 가능 상태로 옵션 조회
     */
    List<ProductOption> findByProductIdAndIsAvailable(Long productId, boolean isAvailable);

    /**
     * 상품 및 배송 유형으로 옵션 조회
     */
    List<ProductOption> findByProductIdAndOptionValue(Long productId, String optionValue);

    /**
     * 상품 ID로 사용 가능한 옵션 조회
     */
    List<ProductOption> findByProductIdAndIsAvailableOrderByDisplayOrderAsc(Long productId, boolean isAvailable);
}
