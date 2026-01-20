package com.flower.api.controller;

import com.flower.product.domain.ProductCategory;
import com.flower.product.domain.Product.DeliveryType;
import com.flower.product.dto.ProductDto;
import com.flower.product.dto.RestockProductRequest;
import com.flower.product.service.ProductQueryService;
import com.flower.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Test
    @DisplayName("Should restock product")
    void shouldRestockProduct() {
        // Given
        ProductService productService = mock(ProductService.class);
        ProductQueryService productQueryService = mock(ProductQueryService.class);
        ProductController productController = new ProductController(productService, productQueryService);

        Long productId = 1L;
        RestockProductRequest request = new RestockProductRequest(50);
        ProductDto productDto = new ProductDto(
            productId, 
            "Test Product", 
            BigDecimal.valueOf(1000), 
            100, 
            "http://image.url", 
            true, 
            true, 
            Collections.emptyList(), 
            ProductCategory.FLOWER_BOUQUET, 
            DeliveryType.PARCEL
        );

        given(productQueryService.getProductById(productId)).willReturn(productDto);

        // When
        ResponseEntity<ProductDto> response = productController.restockProduct(productId, request);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(productId);
        verify(productService).increaseStock(productId, 50);
    }
}
