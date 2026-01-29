package com.flower.api.controller;

import com.flower.api.security.UserPrincipal;
import com.flower.common.exception.BusinessException;
import com.flower.product.domain.Product;
import com.flower.product.dto.CreateProductRequest;
import com.flower.product.dto.ProductDto;
import com.flower.product.dto.UpdateProductRequest;
import com.flower.product.service.ProductQueryService;
import com.flower.product.service.ProductService;
import com.flower.store.service.StoreService;
import com.flower.store.dto.StoreDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/seller/products")
@RequiredArgsConstructor
@Tag(name = "Seller Product", description = "사장님용 상품 관리 API")
@PreAuthorize("hasAnyRole('SELLER', 'STORE_ADMIN', 'ROOT_ADMIN')")
public class SellerProductController {

    private final ProductService productService;
    private final ProductQueryService productQueryService;
    private final StoreService storeService;

    @Operation(summary = "내 가게 상품 등록", description = "사장님이 본인 가게의 상품을 등록합니다.")
    @PostMapping
    public ResponseEntity<ProductDto> createStoreProduct(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CreateProductRequest request) {
        
        // 1. 사장님의 가게 정보 조회
        StoreDto myStore = storeService.getMyStore(principal.getId());
        
        // 2. 상품 생성 후 storeId 연결
        Product product = productService.createProduct(request);
        product.setStoreId(myStore.id());
        productService.updateProduct(product.getId(), product);
        
        // 3. DTO 변환 후 응답
        ProductDto responseDto = productQueryService.getProductById(product.getId());
        log.info("사장님 상품 등록 완료: storeId={}, productId={}", myStore.id(), product.getId());
        
        return ResponseEntity.created(URI.create("/api/v1/seller/products/" + product.getId()))
                .body(responseDto);
    }

    @Operation(summary = "내 가게 상품 목록 조회", description = "본인 가게의 모든 상품을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ProductDto>> getMyStoreProducts(
            @AuthenticationPrincipal UserPrincipal principal) {
        
        StoreDto myStore = storeService.getMyStore(principal.getId());
        List<ProductDto> myProducts = productService.getProductsByStoreId(myStore.id());
        
        return ResponseEntity.ok(myProducts);
    }

    @Operation(summary = "내 가게 상품 상세 조회", description = "본인 가게 상품의 상세 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getMyStoreProduct(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId) {
        
        StoreDto myStore = storeService.getMyStore(principal.getId());
        Product product = productService.getById(productId);
        
        // 권한 검증: 본인 가게 상품인지 확인
        if (!myStore.id().equals(product.getStoreId())) {
            throw new BusinessException("본인 가게의 상품만 조회할 수 있습니다.");
        }
        
        ProductDto responseDto = productQueryService.getProductById(productId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "내 가게 상품 수정", description = "본인 가게 상품 정보를 수정합니다.")
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateMyStoreProduct(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId,
            @RequestBody UpdateProductRequest request) {
        
        StoreDto myStore = storeService.getMyStore(principal.getId());
        Product product = productService.getById(productId);
        
        // 권한 검증
        if (!myStore.id().equals(product.getStoreId())) {
            throw new BusinessException("본인 가게의 상품만 수정할 수 있습니다.");
        }
        
        productService.updateProduct(productId, request);
        ProductDto responseDto = productQueryService.getProductById(productId);
        
        log.info("사장님 상품 수정 완료: storeId={}, productId={}", myStore.id(), productId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "내 가게 상품 삭제", description = "본인 가게 상품을 비활성화 처리합니다.")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteMyStoreProduct(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId) {
        
        StoreDto myStore = storeService.getMyStore(principal.getId());
        Product product = productService.getById(productId);
        
        // 권한 검증
        if (!myStore.id().equals(product.getStoreId())) {
            throw new BusinessException("본인 가게의 상품만 삭제할 수 있습니다.");
        }
        
        productService.deleteProduct(productId);
        log.info("사장님 상품 삭제 완료: storeId={}, productId={}", myStore.id(), productId);
        
        return ResponseEntity.ok().build();
    }
}
