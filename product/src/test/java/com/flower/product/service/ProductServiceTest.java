package com.flower.product.service;

import com.flower.common.exception.EntityNotFoundException;
import com.flower.product.domain.Product;
import com.flower.product.domain.ProductCategory;
import com.flower.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .productCode("TEST-001")
                .name("장미 꽃다발")
                .description("아름다운 빨간 장미 꽃다발")
                .price(new BigDecimal("50000"))
                .discountPrice(new BigDecimal("45000"))
                .stockQuantity(100)
                .category(ProductCategory.FLOWER_GIFT)
                .isActive(true)
                .isFeatured(true)
                .isTrending(false)
                .build();
    }

    @Test
    @DisplayName("Should get product by ID")
    void shouldGetProductById() {
        // 준비
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // 실행
        Product result = productService.getById(1L);

        // 검증
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("장미 꽃다발");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        // 준비
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // 실행 및 검증
        assertThrows(EntityNotFoundException.class, () -> productService.getProductById(999L));
    }

    @Test
    @DisplayName("Should get products by category")
    void shouldGetProductsByCategory() {
        // 준비
        when(productRepository.findActiveProductsByCategoryOrderByTrending(ProductCategory.FLOWER_GIFT))
                .thenReturn(List.of(testProduct));

        // 실행
        List<Product> products = productService.getProductsByCategory(ProductCategory.FLOWER_GIFT);

        // 검증
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getCategory()).isEqualTo(ProductCategory.FLOWER_GIFT);
        verify(productRepository, times(1)).findActiveProductsByCategoryOrderByTrending(ProductCategory.FLOWER_GIFT);
    }

    @Test
    @DisplayName("Should check stock successfully")
    void shouldCheckStockSuccessfully() {
        // 준비
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // 실행
        boolean hasStock = productService.checkStock(1L, 50);

        // 검증
        assertThat(hasStock).isTrue();
    }

    @Test
    @DisplayName("Should decrease stock successfully")
    void shouldDecreaseStockSuccessfully() {
        // 준비
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // 실행
        productService.decreaseStock(1L, 10);

        // 검증
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should get effective price")
    void shouldGetEffectivePrice() {
        // 실행
        BigDecimal effectivePrice = testProduct.getEffectivePrice();

        // 검증
        assertThat(effectivePrice).isEqualTo(new BigDecimal("45000")); // 할인가
        assertThat(testProduct.isOnDiscount()).isTrue();
    }

    @Test
    @DisplayName("Should search products by keyword")
    void shouldSearchProductsByKeyword() {
        // 준비
        when(productRepository.searchProducts("장미")).thenReturn(List.of(testProduct));

        // 실행
        List<Product> results = productService.searchProducts("장미");

        // 검증
        assertThat(results).hasSize(1);
        verify(productRepository, times(1)).searchProducts("장미");
    }
}
