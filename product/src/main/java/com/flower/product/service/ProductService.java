package com.flower.product.service;

import com.flower.common.exception.EntityNotFoundException;
import com.flower.product.domain.Product;
import com.flower.product.domain.ProductAddon;
import com.flower.product.domain.ProductCategory;
import com.flower.product.domain.ProductOption;
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

    @Override
    @Transactional(readOnly = true)
    public Product getById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + productId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getByIds(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }

    @Override
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

    /**
     * 상품 코드로 상품 조회
     */
    @Transactional(readOnly = true)
    public Product getProductByCode(String productCode) {
        return productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + productCode));
    }

    /**
     * 카테고리별 활성 상품 조회
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(ProductCategory category) {
        return productRepository.findActiveProductsByCategoryOrderByTrending(category);
    }

    /**
     * 추천 상품 조회
     */
    @Transactional(readOnly = true)
    public List<Product> getFeaturedProducts() {
        return productRepository.findFeaturedProducts();
    }

    /**
     * 인기 상품 조회
     */
    @Transactional(readOnly = true)
    public List<Product> getTrendingProducts() {
        return productRepository.findTrendingProducts();
    }

    /**
     * 당일 배송 가능 상품 조회
     */
    @Transactional(readOnly = true)
    public List<Product> getSameDayDeliveryProducts() {
        return productRepository.findSameDayDeliveryProducts();
    }

    /**
     * 키워드로 상품 검색
     */
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }

    /**
     * 구매 가능 상품 전체 조회 (재고 있음 & 활성 상태)
     */
    @Transactional(readOnly = true)
    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    /**
     * 카테고리별 구매 가능 상품 조회
     */
    @Transactional(readOnly = true)
    public List<Product> getAvailableProductsByCategory(ProductCategory category) {
        return productRepository.findAvailableProductsByCategory(category);
    }

    /**
     * 신규 상품 생성
     */
    @Transactional
    public Product createProduct(Product product) {
        log.info("신규 상품 생성: {}", product.getName());
        return productRepository.save(product);
    }

    /**
     * 기존 상품 정보 수정
     */
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

    /**
     * 상품 삭제 (활성 상태를 false로 변경하는 Soft Delete)
     */
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = getById(productId);
        log.info("상품 삭제 처리: {}", product.getName());
        product.setIsActive(false);
        productRepository.save(product);
    }

    /**
     * 재고 충분 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean checkStock(Long productId, int quantity) {
        Product product = getById(productId);
        return product.hasSufficientStock(quantity);
    }

    /**
     * 재고 감소
     */
    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        Product product = getById(productId);
        product.decreaseStock(quantity);
        productRepository.save(product);
        log.info("상품 재고 감소: {} - 수량: {}, 남은재고: {}",
                product.getName(), quantity, product.getStockQuantity());
    }

    /**
     * 재고 증가
     */
    @Transactional
    public void increaseStock(Long productId, int quantity) {
        Product product = getById(productId);
        product.increaseStock(quantity);
        productRepository.save(product);
        log.info("상품 재고 증가: {} - 수량: {}, 남은재고: {}",
                product.getName(), quantity, product.getStockQuantity());
    }

    /**
     * 상품 옵션 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ProductOption> getProductOptions(Long productId) {
        return productOptionRepository.findByProductId(productId);
    }

    /**
     * 구매 가능 상품 옵션 조회
     */
    @Transactional(readOnly = true)
    public List<ProductOption> getAvailableProductOptions(Long productId) {
        return productOptionRepository.findByProductIdAndIsAvailableOrderByDisplayOrderAsc(productId, true);
    }

    /**
     * 카테고리별 추가 상품 조회
     */
    @Transactional(readOnly = true)
    public List<ProductAddon> getAddonsByCategory(ProductAddon.Category category) {
        return productAddonRepository.findByCategoryAndIsAvailableOrderByDisplayOrderAsc(category, true);
    }

    /**
     * 구매 가능 추가 상품 전체 조회
     */
    @Transactional(readOnly = true)
    public List<ProductAddon> getAvailableAddons() {
        return productAddonRepository.findByIsAvailableOrderByDisplayOrderAsc(true);
    }

    /**
     * 신규 상품 옵션 생성
     */
    @Transactional
    public ProductOption createProductOption(ProductOption option) {
        log.info("신규 상품 옵션 생성: {}", option.getValue());
        return productOptionRepository.save(option);
    }

    /**
     * 신규 추가 상품 생성
     */
    @Transactional
    public ProductAddon createProductAddon(ProductAddon addon) {
        log.info("신규 추가 상품 생성: {}", addon.getName());
        return productAddonRepository.save(addon);
    }
}
