package com.flower.product.repository;

import com.flower.product.domain.Product;
import com.flower.product.domain.ProductCategory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 상품 엔티티를 위한 리포지토리
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByProductCode(String productCode);

    List<Product> findByCategory(ProductCategory category);

    List<Product> findByCategoryAndIsActive(ProductCategory category, Boolean isActive);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.category = :category ORDER BY p.isTrending DESC, p.createdAt DESC")
    List<Product> findActiveProductsByCategoryOrderByTrending(@Param("category") ProductCategory category);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.isFeatured = true")
    List<Product> findFeaturedProducts();

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.isTrending = true")
    List<Product> findTrendingProducts();

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.isAvailableToday = true")
    List<Product> findSameDayDeliveryProducts();

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Product> searchProducts(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.stockQuantity > 0")
    List<Product> findAvailableProducts();

     @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.category = :category AND p.stockQuantity > 0")
     List<Product> findAvailableProductsByCategory(@Param("category") ProductCategory category);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.deliveryType = :deliveryType")
    List<Product> findByDeliveryType(@Param("deliveryType") com.flower.product.domain.Product.DeliveryType deliveryType);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND :tag IN elements(p.tags)")
    List<Product> findByTag(@Param("tag") String tag);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.category = :category AND p.deliveryType = :deliveryType")
    List<Product> findByCategoryAndDeliveryType(@Param("category") ProductCategory category, @Param("deliveryType") com.flower.product.domain.Product.DeliveryType deliveryType);
     @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE p.storeId = :storeId AND p.isActive = true")
    List<Product> findByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT p FROM Product p WHERE p.storeId = :storeId")
    List<Product> findAllByStoreId(@Param("storeId") Long storeId);
}
