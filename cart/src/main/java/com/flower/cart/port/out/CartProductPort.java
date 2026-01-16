package com.flower.cart.port.out;

import com.flower.cart.dto.ProductInfo;

import java.util.List;
import com.flower.product.dto.ProductOptionDto;

public interface CartProductPort {
    ProductInfo getProductById(Long productId);
    List<ProductOptionDto> getOptionsByIds(List<Long> optionIds);
}
