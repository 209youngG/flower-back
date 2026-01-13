package com.flower.api.controller;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductQueryService productQueryService;

    @InjectMocks
    private ProductController productController;

    @Test
    @DisplayName("Should restock product")
    void shouldRestockProduct() {
        // Given
        Long productId = 1L;
        RestockProductRequest request = new RestockProductRequest(50);
        ProductDto productDto = new ProductDto(productId, "Test Product", BigDecimal.valueOf(1000), true, true);

        given(productQueryService.getProductById(productId)).willReturn(productDto);

        // When
        ResponseEntity<ProductDto> response = productController.restockProduct(productId, request);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(productDto);
        verify(productService).increaseStock(productId, 50);
    }
}
