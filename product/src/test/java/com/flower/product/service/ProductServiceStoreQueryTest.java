package com.flower.product.service;

import com.flower.product.domain.Product;
import com.flower.product.domain.ProductCategory;
import com.flower.product.dto.ProductDto;
import com.flower.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service - Store 기반 상품 조회 Tests")
class ProductServiceStoreQueryTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private com.flower.product.repository.ProductOptionRepository productOptionRepository;

    @Mock
    private com.flower.product.repository.ProductAddonRepository productAddonRepository;

    @InjectMocks
    private ProductService productService;

    private Product storeProduct1;
    private Product storeProduct2;
    private Product otherStoreProduct;

    @BeforeEach
    void setUp() {
        storeProduct1 = Product.builder()
                .id(1L)
                .storeId(100L)
                .productCode("STORE-001")
                .name("우리 가게 장미 꽃다발")
                .description("당일 배송 가능한 신선한 장미")
                .price(new BigDecimal("50000"))
                .stockQuantity(50)
                .category(ProductCategory.FLOWER_BOUQUET)
                .isActive(true)
                .build();

        storeProduct2 = Product.builder()
                .id(2L)
                .storeId(100L)
                .productCode("STORE-002")
                .name("우리 가게 튤립 꽃다발")
                .description("화사한 봄 분위기")
                .price(new BigDecimal("40000"))
                .stockQuantity(30)
                .category(ProductCategory.FLOWER_BOUQUET)
                .isActive(true)
                .build();

        otherStoreProduct = Product.builder()
                .id(3L)
                .storeId(200L)
                .productCode("OTHER-001")
                .name("다른 가게 상품")
                .price(new BigDecimal("30000"))
                .stockQuantity(10)
                .category(ProductCategory.FLOWER_GIFT)
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("특정 가게의 활성 상품만 조회")
    void should_getProductsByStoreId_when_validStoreId() {
        // given
        Long storeId = 100L;
        given(productRepository.findByStoreId(storeId))
                .willReturn(Arrays.asList(storeProduct1, storeProduct2));

        // when
        List<ProductDto> products = productService.getProductsByStoreId(storeId);

        // then
        assertThat(products).hasSize(2);
        assertThat(products).allMatch(p -> p.storeId().equals(100L));
        assertThat(products).extracting(ProductDto::name)
                .containsExactlyInAnyOrder("우리 가게 장미 꽃다발", "우리 가게 튤립 꽃다발");
        verify(productRepository).findByStoreId(storeId);
    }

    @Test
    @DisplayName("가게에 상품이 없으면 빈 리스트 반환")
    void should_returnEmptyList_when_storeHasNoProducts() {
        // given
        Long emptyStoreId = 999L;
        given(productRepository.findByStoreId(emptyStoreId))
                .willReturn(List.of());

        // when
        List<ProductDto> products = productService.getProductsByStoreId(emptyStoreId);

        // then
        assertThat(products).isEmpty();
        verify(productRepository).findByStoreId(emptyStoreId);
    }

    @Test
    @DisplayName("다른 가게 상품은 조회되지 않음")
    void should_notReturnOtherStoreProducts_when_queryingSpecificStore() {
        // given
        Long storeId = 100L;
        given(productRepository.findByStoreId(storeId))
                .willReturn(Arrays.asList(storeProduct1, storeProduct2));

        // when
        List<ProductDto> products = productService.getProductsByStoreId(storeId);

        // then
        assertThat(products).noneMatch(p -> p.storeId().equals(200L));
        assertThat(products).noneMatch(p -> p.name().equals("다른 가게 상품"));
    }
}
