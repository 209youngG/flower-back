package com.flower.cart.domain;

import com.flower.cart.dto.ProductInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItemOption> options = new ArrayList<>();

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (this.quantity <= amount) {
            throw new IllegalArgumentException("Cannot decrease quantity below 1");
        }
        this.quantity -= amount;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        this.quantity = quantity;
    }

    public void addOption(CartItemOption option) {
        if (!options.contains(option)) {
            options.add(option);
        }
    }

    public void removeOption(CartItemOption option) {
        options.remove(option);
    }
    
    public void clearOptions() {
        options.clear();
    }

    public BigDecimal calculateTotalPrice(ProductInfo productInfo) {
        BigDecimal total = productInfo.getEffectivePrice().multiply(new BigDecimal(quantity));

        for (CartItemOption option : options) {
            total = total.add(option.getPriceAdjustment() != null ? option.getPriceAdjustment() : BigDecimal.ZERO);
        }

        return total;
    }

    public BigDecimal getTotalPrice() {
        if (unitPrice == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = unitPrice.multiply(new BigDecimal(quantity));

        for (CartItemOption option : options) {
            total = total.add(option.getPriceAdjustment() != null ? option.getPriceAdjustment() : BigDecimal.ZERO);
        }

        return total;
    }
}
