package com.flower.order.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item_options")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(name = "product_option_id")
    private Long productOptionId;

    @Column(name = "product_addon_id")
    private Long productAddonId;
    
    @Column(name = "option_name", nullable = false)
    private String optionName; // 구매 시점의 옵션명 스냅샷

    @Column(name = "price_adjustment", precision = 10, scale = 2)
    private BigDecimal priceAdjustment; // 구매 시점의 가격 스냅샷
}
