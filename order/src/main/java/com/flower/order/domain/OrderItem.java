package com.flower.order.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItemOption> options = new ArrayList<>();

    @Column(name = "discount_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @PrePersist
    @PreUpdate
    protected void calculateTotal() {
        if (unitPrice != null && quantity != null) {
            BigDecimal subtotal = unitPrice.multiply(new BigDecimal(quantity));
            
            // 옵션 가격 추가
            BigDecimal optionsPrice = options.stream()
                    .map(opt -> opt.getPriceAdjustment() != null ? opt.getPriceAdjustment() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(new BigDecimal(quantity)); // 단위당 옵션 적용
            
            this.totalPrice = subtotal.add(optionsPrice).subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
        }
    }

    public void addOption(OrderItemOption option) {
        options.add(option);
        option.setOrderItem(this);
    }

    public BigDecimal getTotalPrice() {
        if (totalPrice != null) {
            return totalPrice;
        }
        // 영속화되기 전 대체 계산
        BigDecimal baseTotal = unitPrice.multiply(new BigDecimal(quantity));
        BigDecimal optionsTotal = options.stream()
                .map(opt -> opt.getPriceAdjustment() != null ? opt.getPriceAdjustment() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(new BigDecimal(quantity));
                
        return baseTotal.add(optionsTotal).subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
    }
}
