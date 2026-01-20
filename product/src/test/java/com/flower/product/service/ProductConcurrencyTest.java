package com.flower.product.service;

import com.flower.product.domain.Product;
import com.flower.product.domain.ProductCategory;
import com.flower.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = com.flower.product.TestProductApplication.class)
class ProductConcurrencyTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long productId;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .name("테스트 장미")
                .productCode("TEST-ROSE")
                .description("동시성 테스트용")
                .price(BigDecimal.valueOf(10000))
                .stockQuantity(100)
                .category(ProductCategory.FLOWER_BOUQUET)
                .deliveryType(Product.DeliveryType.PARCEL)
                .isActive(true)
                .build();
        testProduct = productRepository.save(testProduct);
        productId = testProduct.getId();
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("동시에 100명이 주문하면 재고가 0이 되어야 한다")
    void decreaseStock_Concurrency() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        // 성공 및 실패 횟수 추적
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productService.decreaseStock(productId, 1);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("재고 차감 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Product product = productRepository.findById(productId).orElseThrow();
        
        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("실패 횟수: " + failCount.get());
        System.out.println("남은 재고: " + product.getStockQuantity());

        assertThat(product.getStockQuantity()).isEqualTo(0);
        assertThat(successCount.get()).isEqualTo(100);
    }

    @Test
    @DisplayName("재고보다 많은 주문이 들어오면 남은 재고만큼만 성공하고 나머지는 실패해야 한다")
    void decreaseStock_NotEnoughStock() throws InterruptedException {
        // given
        int initialStock = 10;
        testProduct = productRepository.findById(productId).orElseThrow();
        testProduct.setStockQuantity(initialStock);
        productRepository.save(testProduct);

        int threadCount = 30; // 재고보다 많은 요청
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productService.decreaseStock(productId, 1);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Product product = productRepository.findById(productId).orElseThrow();
        
        System.out.println("Out of Stock Test - 성공: " + successCount.get() + ", 실패: " + failCount.get());

        assertThat(product.getStockQuantity()).isEqualTo(0);
        assertThat(successCount.get()).isEqualTo(initialStock);
        assertThat(failCount.get()).isEqualTo(threadCount - initialStock);
    }
}
