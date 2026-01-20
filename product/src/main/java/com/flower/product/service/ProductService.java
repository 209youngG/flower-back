package com.flower.product.service;

import com.flower.common.exception.EntityNotFoundException;
import com.flower.product.domain.Product;
import com.flower.product.domain.ProductAddon;
import com.flower.product.domain.ProductCategory;
import com.flower.product.domain.ProductOption;
import com.flower.product.dto.*;
import com.flower.product.repository.ProductAddonRepository;
import com.flower.product.repository.ProductOptionRepository;
import com.flower.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements ProductQueryService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductAddonRepository productAddonRepository;

    // --- Query Methods ---

    @Transactional(readOnly = true)
    public Product getById(Long productId) {
        return findProductById(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long productId) {
        return toDto(getById(productId));
    }

    @Transactional(readOnly = true)
    public List<Product> getByIds(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, ProductDto> getProductsMapByIds(List<Long> productIds) {
        return getByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, this::toDto));
    }

    @Transactional(readOnly = true)
    public Map<Long, Product> getMapByIds(List<Long> productIds) {
        return getByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long productId) {
        return productRepository.existsById(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Product getProductByCode(String productCode) {
        return productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + productCode));
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(ProductCategory category) {
        return productRepository.findActiveProductsByCategoryOrderByTrending(category);
    }

    @Transactional(readOnly = true)
    public List<Product> getFeaturedProducts() {
        return productRepository.findFeaturedProducts();
    }

    @Transactional(readOnly = true)
    public List<Product> getTrendingProducts() {
        return productRepository.findTrendingProducts();
    }

    @Transactional(readOnly = true)
    public List<Product> getSameDayDeliveryProducts() {
        return productRepository.findSameDayDeliveryProducts();
    }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }

    @Transactional(readOnly = true)
    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    @Transactional(readOnly = true)
    public List<Product> getAvailableProductsByCategory(ProductCategory category) {
        return productRepository.findAvailableProductsByCategory(category);
    }

    // --- Command Methods ---

    @Transactional
    public Product createProduct(CreateProductRequest request) {
        log.info("신규 상품 생성 요청: {}", request.name());

        Product product = buildProductFromRequest(request);
        Product savedProduct = productRepository.save(product);

        saveProductOptions(request.options(), savedProduct);

        return savedProduct;
    }
    
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long productId, UpdateProductRequest request) {
        Product existingProduct = findProductById(productId);

        log.info("상품 정보 수정: {}", existingProduct.getName());
        
        updateProductFields(existingProduct, request);
        updateProductOptions(existingProduct, request.options());

        return productRepository.save(existingProduct);
    }
    
    @Transactional
    public Product updateProduct(Long productId, Product product) {
         return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = findProductById(productId);
        log.info("상품 삭제 처리: {}", product.getName());
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public boolean checkStock(Long productId, int quantity) {
        Product product = findProductById(productId);
        return product.hasSufficientStock(quantity);
    }

    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + productId));
        
        product.decreaseStock(quantity);
        productRepository.save(product);
        
        log.info("상품 재고 감소: {} - 수량: {}, 남은재고: {}",
                product.getName(), quantity, product.getStockQuantity());
    }

    @Transactional
    public void increaseStock(Long productId, int quantity) {
        Product product = findProductById(productId);
        product.increaseStock(quantity);
        productRepository.save(product);
        log.info("상품 재고 증가: {} - 수량: {}, 남은재고: {}",
                product.getName(), quantity, product.getStockQuantity());
    }

    // --- Options & Addons ---

    @Override
    @Transactional(readOnly = true)
    public List<ProductOptionDto> getOptionsByIds(List<Long> optionIds) {
        if (optionIds == null || optionIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return productOptionRepository.findAllById(optionIds).stream()
            .map(this::toOptionDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductOption> getProductOptions(Long productId) {
        return productOptionRepository.findByProductId(productId);
    }

    @Transactional(readOnly = true)
    public List<ProductOption> getAvailableProductOptions(Long productId) {
        return productOptionRepository.findByProductIdAndIsAvailableOrderByDisplayOrderAsc(productId, true);
    }

    @Transactional(readOnly = true)
    public List<ProductAddon> getAddonsByCategory(ProductAddon.Category category) {
        return productAddonRepository.findByCategoryAndIsAvailableOrderByDisplayOrderAsc(category, true);
    }

    @Transactional(readOnly = true)
    public List<ProductAddon> getAvailableAddons() {
        return productAddonRepository.findByIsAvailableOrderByDisplayOrderAsc(true);
    }

    @Transactional
    public ProductOption createProductOption(ProductOption option) {
        log.info("신규 상품 옵션 생성: {}", option.getOptionValue());
        return productOptionRepository.save(option);
    }

    @Transactional
    public ProductAddon createProductAddon(ProductAddon addon) {
        log.info("신규 추가 상품 생성: {}", addon.getName());
        return productAddonRepository.save(addon);
    }

    // --- Private Helper Methods ---

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + productId));
    }

    private Product buildProductFromRequest(CreateProductRequest request) {
        return Product.builder()
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
    }

    private void saveProductOptions(List<CreateProductOptionRequest> optionRequests, Product product) {
        if (optionRequests != null && !optionRequests.isEmpty()) {
            List<ProductOption> options = optionRequests.stream()
                    .map(optRequest -> ProductOption.builder()
                            .product(product)
                            .name(optRequest.name())
                            .optionValue(optRequest.value())
                            .priceAdjustment(optRequest.priceAdjustment())
                            .stockQuantity(optRequest.stockQuantity())
                            .displayOrder(optRequest.displayOrder())
                            .isAvailable(true)
                            .build())
                    .collect(Collectors.toList());
            productOptionRepository.saveAll(options);
        }
    }

    private void updateProductFields(Product product, UpdateProductRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setDiscountPrice(request.discountPrice());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(request.category());
        product.setIsActive(request.isActive());
        product.setIsAvailableToday(request.isAvailableToday());
        product.setThumbnailUrl(request.thumbnailUrl());
        product.setDeliveryType(request.deliveryType());
    }

    private void updateProductOptions(Product product, List<CreateProductOptionRequest> optionRequests) {
        if (optionRequests != null) {
            product.getOptions().clear();
            
            for (CreateProductOptionRequest optReq : optionRequests) {
                ProductOption option = ProductOption.builder()
                        .product(product)
                        .name(optReq.name())
                        .optionValue(optReq.value())
                        .priceAdjustment(optReq.priceAdjustment())
                        .stockQuantity(optReq.stockQuantity())
                        .displayOrder(optReq.displayOrder())
                        .isAvailable(true)
                        .build();
                product.getOptions().add(option);
            }
        }
    }

    private ProductDto toDto(Product product) {
        List<ProductOptionDto> optionDtos = product.getOptions().stream()
                .map(this::toOptionDto)
                .collect(Collectors.toList());

        return new ProductDto(
            product.getId(),
            product.getName(),
            product.getEffectivePrice(),
            product.getStockQuantity(),
            product.getThumbnailUrl(),
            product.getIsActive(),
            product.getIsAvailableToday(),
            optionDtos,
            product.getCategory(),
            product.getDeliveryType()
        );
    }

    private ProductOptionDto toOptionDto(ProductOption opt) {
        return new ProductOptionDto(
                opt.getId(),
                opt.getName(),
                opt.getOptionValue(),
                opt.getPriceAdjustment()
        );
    }
}
