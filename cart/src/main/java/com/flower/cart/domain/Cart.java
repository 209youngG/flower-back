package com.flower.cart.domain;

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
@Table(name = "carts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cartKey;

    @Column(name = "member_id")
    private Long memberId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "total_quantity")
    @Builder.Default
    private Integer totalQuantity = 0;

    @Column(name = "total_price", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalPrice = BigDecimal.ZERO;

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
        calculateTotals();
    }

    public CartItem addItem(Long productId, BigDecimal unitPrice, int quantity) {
        CartItem existingItem = findItemByProductId(productId);

        if (existingItem != null) {
            existingItem.increaseQuantity(quantity);
            calculateTotals();
            return existingItem;
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(this)
                    .productId(productId)
                    .unitPrice(unitPrice)
                    .quantity(quantity)
                    .build();

            items.add(newItem);
            calculateTotals();
            return newItem;
        }
    }

    public void removeItem(Long itemId) {
        items.removeIf(item -> item.getId().equals(itemId));
        calculateTotals();
    }

    public void updateItemQuantity(Long itemId, int quantity) {
        CartItem item = findItemById(itemId);
        if (item != null) {
            item.setQuantity(quantity);
            calculateTotals();
        }
    }

    public void clear() {
        items.clear();
        calculateTotals();
    }

    public CartItem findItemById(Long itemId) {
        return items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    public CartItem findItemByProductId(Long productId) {
        return items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getItemCount() {
        return items.size();
    }

    private void calculateTotals() {
        this.totalQuantity = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        this.totalPrice = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
