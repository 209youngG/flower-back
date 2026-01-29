package com.flower.product.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id")
    private Long storeId;

    @Column(nullable = false, unique = true)
    private String productCode;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "discount_price", precision = 10, scale = 2)
    private BigDecimal discountPrice;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(name = "min_order_quantity")
    @Builder.Default
    private Integer minOrderQuantity = 1;

    @Column(name = "max_order_quantity")
    private Integer maxOrderQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private ProductSize size;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_trending")
    @Builder.Default
    private Boolean isTrending = false;

    @Column(name = "is_available_today")
    @Builder.Default
    private Boolean isAvailableToday = false;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "review_count")
    @Builder.Default
    private Long reviewCount = 0L;

    @Column(name = "total_rating")
    @Builder.Default
    private Long totalRating = 0L;

    @Column(name = "average_rating")
    @Builder.Default
    private Double averageRating = 0.0;

    @ElementCollection
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryType deliveryType;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductOption> options = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (productCode == null) {
            productCode = generateProductCode();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateProductCode() {
        return "PRD-" + System.currentTimeMillis();
    }

    public boolean hasSufficientStock(int quantity) {
        return stockQuantity >= quantity;
    }

    public void decreaseStock(int quantity) {
        if (!hasSufficientStock(quantity)) {
            throw new IllegalStateException("재고가 부족합니다. 현재재고: " + stockQuantity + ", 요청수량: " + quantity);
        }
        this.stockQuantity -= quantity;
    }

    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public BigDecimal getEffectivePrice() {
        return discountPrice != null ? discountPrice : price;
    }

    public boolean isOnDiscount() {
        return discountPrice != null && discountPrice.compareTo(price) < 0;
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    @Getter
    @AllArgsConstructor
    public enum DeliveryType {
        QUICK("퀵배송"),
        PARCEL("택배배송"),
        MIXED("혼합배송");

        private final String description;
    }

    public void addReviewRating(int rating) {
        this.reviewCount++;
        this.totalRating += rating;
        calculateAverageRating();
    }

    public void removeReviewRating(int rating) {
        if (this.reviewCount > 0) {
            this.reviewCount--;
            this.totalRating -= rating;
            if (this.totalRating < 0) this.totalRating = 0L;
            calculateAverageRating();
        }
    }

    public void updateReviewRating(int oldRating, int newRating) {
        this.totalRating = this.totalRating - oldRating + newRating;
        if (this.totalRating < 0) this.totalRating = 0L;
        calculateAverageRating();
    }

    public void updateReviewStats(Long count, Long totalScore) {
        this.reviewCount = count;
        this.totalRating = totalScore;
        calculateAverageRating();
    }

    private void calculateAverageRating() {
        if (this.reviewCount > 0) {
            this.averageRating = (double) this.totalRating / this.reviewCount;
            this.averageRating = Math.round(this.averageRating * 10.0) / 10.0;
        } else {
            this.averageRating = 0.0;
        }
    }
}
