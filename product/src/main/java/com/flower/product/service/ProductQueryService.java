package com.flower.product.service;

import com.flower.product.dto.ProductDto;

import java.util.List;
import java.util.Map;

public interface ProductQueryService {

    ProductDto getProductById(Long productId);

    Map<Long, ProductDto> getProductsMapByIds(List<Long> productIds);

    boolean existsById(Long productId);

    List<ProductDto> getAllProducts();
}
