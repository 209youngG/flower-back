package com.flower.api.controller;

import com.flower.common.exception.EntityNotFoundException;
import com.flower.product.domain.Product;
import com.flower.product.dto.CreateProductRequest;
import com.flower.product.dto.ProductDto;
import com.flower.product.dto.UpdateProductRequest;
import com.flower.product.service.ProductQueryService;
import com.flower.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "상품 관리 API")
public class ProductController {

    private final ProductService productService;
    private final ProductQueryService productQueryService;

    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody CreateProductRequest request) {
        // DTO -> Entity 변환
        Product product = Product.builder()
                .name(request.name())
                .productCode(request.productCode())
                .description(request.description())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .category(request.category())
                .deliveryType(request.deliveryType())
                .thumbnailUrl(request.thumbnailUrl())
                .isActive(true)
                .build();

        Product savedProduct = productService.createProduct(product);
        ProductDto responseDto = productQueryService.getProductById(savedProduct.getId());
        
        return ResponseEntity.created(URI.create("/api/v1/products/" + savedProduct.getId())).body(responseDto);
    }

    @Operation(summary = "상품 수정", description = "기존 상품 정보를 수정합니다.")
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long productId,
            @RequestBody UpdateProductRequest request) {
        
        // 업데이트를 위한 임시 Entity 생성 (Service가 Entity를 요구하므로)
        // 실제로는 Service가 DTO를 받도록 리팩토링하는 것이 좋음
        Product updateInfo = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .discountPrice(request.discountPrice())
                .stockQuantity(request.stockQuantity())
                .category(request.category())
                .deliveryType(request.deliveryType())
                .isActive(request.isActive())
                .isAvailableToday(request.isAvailableToday())
                .thumbnailUrl(request.thumbnailUrl())
                .build();

        productService.updateProduct(productId, updateInfo);
        
        ProductDto responseDto = productQueryService.getProductById(productId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "상품 삭제", description = "상품을 삭제(비활성화) 처리합니다.")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "상품 상세 조회", description = "상품 상세 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long productId) {
        ProductDto product = productQueryService.getProductById(productId);
        return ResponseEntity.ok(product);
    }
}
