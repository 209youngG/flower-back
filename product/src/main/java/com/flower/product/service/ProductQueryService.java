package com.flower.product.service;

import com.flower.product.domain.Product;

import java.util.List;
import java.util.Map;

public interface ProductQueryService {

    Product getById(Long productId);

    List<Product> getByIds(List<Long> productIds);

    Map<Long, Product> getMapByIds(List<Long> productIds);

    boolean existsById(Long productId);
}
