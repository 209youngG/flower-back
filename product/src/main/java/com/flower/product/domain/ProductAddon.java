package com.flower.product.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 추가 상품 엔티티 (케이크, 화병, 카드 등)
 */
@Entity
@Table(name = "product_addons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAddon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Category category;  // 예: 케이크, 화병, 카드, 가위, 초콜릿

    /**
     * 추가 상품 카테고리 Enum
     */
    public enum Category {
        CAKE,        // 케이크
        VASE,         // 화병
        SCISSORS,     // 꽃가위
        CARD,         // 카드/리본
        CHOCOLATE,   // 초콜릿/사탕
        GIFT          // 기타 선물
    }

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
