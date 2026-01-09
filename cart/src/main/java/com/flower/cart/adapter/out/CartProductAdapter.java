package com.flower.cart.adapter.out;

import com.flower.cart.dto.ProductInfo;
import com.flower.cart.port.out.CartProductPort;
import com.flower.product.domain.Product;
import com.flower.product.service.ProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartProductAdapter implements CartProductPort {

    private final ProductQueryService productQueryService;

    @Override
    public ProductInfo getProductById(Long productId) {
        Product product = productQueryService.getById(productId);
        return ProductInfo.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .effectivePrice(product.getEffectivePrice())
                .stockQuantity(product.getStockQuantity())
                .isActive(product.getIsActive())
                .build();
    }
}
