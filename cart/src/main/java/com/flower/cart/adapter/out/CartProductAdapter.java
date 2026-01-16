package com.flower.cart.adapter.out;

import com.flower.cart.dto.ProductInfo;
import com.flower.cart.port.out.CartProductPort;
import com.flower.product.dto.ProductDto;
import com.flower.product.service.ProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import com.flower.product.dto.ProductOptionDto;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CartProductAdapter implements CartProductPort {

    private final ProductQueryService productQueryService;

    @Override
    public ProductInfo getProductById(Long productId) {
        ProductDto product = productQueryService.getProductById(productId);
        
        return ProductInfo.builder()
                .id(product.id())
                .name(product.name())
                .price(product.price()) 
                .effectivePrice(product.price()) 
                .stockQuantity(product.stockQuantity())
                .isActive(product.isActive())
                .build();
    }

    @Override
    public List<ProductOptionDto> getOptionsByIds(List<Long> optionIds) {
        return productQueryService.getOptionsByIds(optionIds);
    }
}
