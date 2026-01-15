package com.flower.product.service;

import com.flower.common.exception.EntityNotFoundException;
import com.flower.product.domain.Product;
import com.flower.product.domain.ProductAddon;
import com.flower.product.domain.ProductCategory;
import com.flower.product.domain.ProductOption;
import com.flower.product.dto.CreateProductOptionRequest;
import com.flower.product.dto.CreateProductRequest;
import com.flower.product.dto.ProductDto;
import com.flower.product.dto.ProductOptionDto;
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

    @Transactional(readOnly = true)
    public Product getById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + productId));
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
        List<Product> products = getByIds(productIds);
        return products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
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

    private ProductDto toDto(Product product) {
        return new ProductDto(
            product.getId(),
            product.getName(),
            product.getEffectivePrice(),
            product.getStockQuantity(),
            product.getThumbnailUrl(),
            product.getIsActive(),
            product.getIsAvailableToday()
        );
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

    @Transactional
    public Product createProduct(CreateProductRequest request) {
        log.info("신규 상품 생성 요청: {}", request.name());

        // 1. 상품 엔티티 생성 및 저장
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
        
        Product savedProduct = productRepository.save(product);

        // 2. 상품 옵션 저장
        if (request.options() != null && !request.options().isEmpty()) {
            List<ProductOption> options = request.options().stream()
                    .map(optRequest -> ProductOption.builder()
                            .product(savedProduct)
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

        return savedProduct;
    }
    
    // 테스트용 등에서 Entity 직접 저장이 필요한 경우를 위해 유지 (오버로딩)
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long productId, Product product) {
        Product existingProduct = getById(productId);

        log.info("상품 정보 수정: {}", existingProduct.getName());

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDiscountPrice(product.getDiscountPrice());
        existingProduct.setStockQuantity(product.getStockQuantity());
        existingProduct.setMinOrderQuantity(product.getMinOrderQuantity());
        existingProduct.setMaxOrderQuantity(product.getMaxOrderQuantity());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setIsActive(product.getIsActive());
        existingProduct.setIsFeatured(product.getIsFeatured());
        existingProduct.setIsTrending(product.getIsTrending());
        existingProduct.setIsAvailableToday(product.getIsAvailableToday());
        existingProduct.setThumbnailUrl(product.getThumbnailUrl());

        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = getById(productId);
        log.info("상품 삭제 처리: {}", product.getName());
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public boolean checkStock(Long productId, int quantity) {
        Product product = getById(productId);
        return product.hasSufficientStock(quantity);
    }

    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        Product product = getById(productId);
        product.decreaseStock(quantity);
        productRepository.save(product);
        log.info("상품 재고 감소: {} - 수량: {}, 남은재고: {}",
                product.getName(), quantity, product.getStockQuantity());
    }

    @Transactional
    public void increaseStock(Long productId, int quantity) {
        Product product = getById(productId);
        product.increaseStock(quantity);
        productRepository.save(product);
        log.info("상품 재고 증가: {} - 수량: {}, 남은재고: {}",
                product.getName(), quantity, product.getStockQuantity());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductOptionDto> getOptionsByIds(List<Long> optionIds) {
        if (optionIds == null || optionIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return productOptionRepository.findAllById(optionIds).stream()
            .map(opt -> new ProductOptionDto(
                opt.getId(),
                opt.getName(),
                opt.getOptionValue(),
                opt.getPriceAdjustment()
            ))
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
}
