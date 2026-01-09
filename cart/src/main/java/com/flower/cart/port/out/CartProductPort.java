package com.flower.cart.port.out;

import com.flower.cart.dto.ProductInfo;

public interface CartProductPort {
    ProductInfo getProductById(Long productId);
}
