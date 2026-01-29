# TDD Guidelines - flower-back (Spring Boot)

**Last Updated:** 2026-01-29  
**Purpose:** Enforce Test-Driven Development across all feature development

---

## 🔴 Red-Green-Refactor 원칙

모든 프로덕션 코드는 **반드시 실패하는 테스트 먼저 작성** 후 구현합니다.

```
🔴 RED    → 실패하는 테스트 작성
🟢 GREEN  → 테스트를 통과하는 최소 코드 작성
🔵 REFACTOR → 코드 개선 (테스트는 계속 Green 유지)
```

---

## 📋 TDD Workflow

### 1️⃣ RED: 실패하는 테스트 작성

**예시: 사장님용 상품 조회 기능**

```java
// product/src/test/java/com/flower/product/service/ProductServiceTest.java

@Test
@DisplayName("사장님이 본인 가게 상품만 조회할 수 있다")
void should_getProductsByStoreId_when_validStoreId() {
    // given
    Long storeId = 100L;
    
    // when
    List<ProductDto> products = productService.getProductsByStoreId(storeId);
    
    // then
    assertThat(products).isNotNull();
    assertThat(products).allMatch(p -> p.storeId().equals(100L));
}
```

**실행 결과:**
```bash
./gradlew :product:test --tests "ProductServiceTest"
# ❌ Method getProductsByStoreId() does not exist
```

---

### 2️⃣ GREEN: 최소한의 구현

```java
// ProductService.java
@Transactional(readOnly = true)
public List<ProductDto> getProductsByStoreId(Long storeId) {
    return productRepository.findByStoreId(storeId).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
}
```

**실행 결과:**
```bash
./gradlew :product:test --tests "ProductServiceTest"
# ✅ Test passed
```

---

### 3️⃣ REFACTOR: 코드 개선

```java
// Extract to ProductQueryService interface
public interface ProductQueryService {
    List<ProductDto> getProductsByStoreId(Long storeId);
}

// Add caching if needed
@Cacheable("storeProducts")
public List<ProductDto> getProductsByStoreId(Long storeId) {
    // ...
}
```

**검증:**
```bash
./gradlew :product:test
# ✅ All tests still pass
```

---

## 🧪 테스트 종류별 가이드

### 1. Unit Tests (단위 테스트)

**대상:** Service, Domain Logic, Util 클래스  
**도구:** JUnit 5 + Mockito + AssertJ

```java
@ExtendWith(MockitoExtension.class)
class StoreServiceTest {
    
    @Mock
    private StoreRepository storeRepository;
    
    @InjectMocks
    private StoreService storeService;
    
    @Test
    @DisplayName("매장 등록 시 상태는 PENDING이어야 한다")
    void should_createStoreWithPendingStatus_when_registered() {
        // given
        RegisterStoreRequest request = new RegisterStoreRequest(...);
        given(storeRepository.save(any())).willAnswer(i -> i.getArgument(0));
        
        // when
        Long storeId = storeService.registerStore(1L, request);
        
        // then
        verify(storeRepository).save(argThat(store -> 
            store.getStatus() == StoreStatus.PENDING
        ));
    }
}
```

---

### 2. Integration Tests (통합 테스트)

**대상:** Repository, Controller (DB 연동)  
**도구:** `@SpringBootTest`, `@DataJpaTest`

```java
@DataJpaTest
class ProductRepositoryTest {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Test
    @DisplayName("특정 가게의 상품만 조회되어야 한다")
    void should_findOnlyStoreProducts_when_queryByStoreId() {
        // given
        Product product1 = createProduct(100L);
        Product product2 = createProduct(200L);
        productRepository.saveAll(List.of(product1, product2));
        
        // when
        List<Product> result = productRepository.findByStoreId(100L);
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStoreId()).isEqualTo(100L);
    }
}
```

---

### 3. Controller Tests (API 테스트)

**도구:** `@WebMvcTest`, MockMvc

```java
@WebMvcTest(SellerProductController.class)
class SellerProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductService productService;
    
    @MockBean
    private StoreService storeService;
    
    @Test
    @DisplayName("사장님이 본인 가게 상품 목록을 조회할 수 있다")
    @WithMockUser(roles = "SELLER")
    void should_getMyProducts_when_authenticated() throws Exception {
        // given
        Long storeId = 100L;
        given(storeService.getMyStore(anyLong()))
            .willReturn(new StoreDto(storeId, ...));
        given(productService.getProductsByStoreId(storeId))
            .willReturn(List.of(...));
        
        // when & then
        mockMvc.perform(get("/api/v1/seller/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }
}
```

---

## 📏 커버리지 목표

- **Service Layer**: 최소 80% 커버리지
- **Domain Logic**: 100% 커버리지 (비즈니스 규칙)
- **Controller**: 주요 엔드포인트 Happy Path + Error Case

---

## 🚫 TDD 위반 사례

### ❌ 나쁜 예시 (Test-Last)

```java
// 1. 먼저 Controller 작성
@PostMapping
public ResponseEntity<ProductDto> createProduct(...) {
    // 구현 완료
}

// 2. 나중에 테스트 작성
@Test
void testCreateProduct() { ... }
```

### ✅ 좋은 예시 (Test-First)

```java
// 1. 먼저 실패하는 테스트 작성
@Test
void should_createProduct_when_validRequest() {
    // 아직 메서드 없음 → 컴파일 에러
    productService.createProduct(request);
}

// 2. 테스트를 통과시키기 위해 구현
public Product createProduct(CreateProductRequest request) {
    // 최소 구현
}
```

---

## 🛠️ TDD 명령어

### 테스트 실행
```bash
# 전체 모듈 테스트
./gradlew test

# 특정 모듈만
./gradlew :product:test

# 특정 클래스만
./gradlew :product:test --tests "ProductServiceTest"

# 특정 메서드만
./gradlew :product:test --tests "ProductServiceTest.should_getProductsByStoreId*"
```

### 커버리지 확인
```bash
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

---

## 📚 참고 자료

- [AGENTS.md - Workflow Protocol](./AGENTS.md)
- JUnit 5: https://junit.org/junit5/docs/current/user-guide/
- Mockito: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- AssertJ: https://assertj.github.io/doc/

---

## ✅ Phase 2부터 적용

앞으로 모든 새로운 기능은:
1. 🔴 테스트 먼저 (Red)
2. 🟢 구현 (Green)
3. 🔵 리팩토링 (Refactor)

**No Test, No Merge!**
