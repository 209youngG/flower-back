# Phase 2: Flori Sommelier (AI Curation) 작업 계획서 🍷

**시작일:** TBD  
**목표 완료일:** TBD (예상 14일)  
**전제조건:** ✅ Phase 1 완료 (O2O Platform Base)  
**개발 원칙:** 🔴 **모든 코드는 TDD로 작성** (Red-Green-Refactor)

---

## 🎯 Phase 2 목표

**비즈니스 목표:**
- 사용자가 "누구에게, 왜, 어떤 분위기로, 얼마의 예산으로" 꽃을 선물하고 싶은지 입력하면
- AI가 최적의 꽃을 추천하고, 감동적인 메시지까지 자동 생성해주는 서비스 구현

**핵심 가치:**
- 꽃 초보자도 쉽게 완벽한 선물 가능
- 플로리스트의 전문 지식을 AI로 민주화
- "번역" - 플로리스트의 언어를 소비자의 감성 언어로

---

## 📋 백엔드 작업 목록

### 🔴 1. Curation Engine (큐레이션 엔진)

#### 1.1 Seasonality Check (제철 꽃 필터링)

**목표:** 월별 제철 꽃 데이터베이스를 구축하고 계절에 맞는 꽃만 추천

**작업 내용:**

**A. DB 설계 (1일차)**
```sql
CREATE TABLE seasonal_flowers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    month INT NOT NULL,                    -- 1~12
    flower_name VARCHAR(100) NOT NULL,     -- 장미, 튤립, 국화 등
    description TEXT,
    peak_season BOOLEAN DEFAULT FALSE,     -- 가장 좋은 시기
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_month ON seasonal_flowers(month);
```

- [ ] **TDD: Step 1 (Red)** - `SeasonalityServiceTest` 작성
  ```java
  @Test
  @DisplayName("1월에는 동백, 수선화가 조회되어야 한다")
  void should_returnJanuaryFlowers_when_monthIsJanuary() {
      // given
      int month = 1;
      
      // when
      List<String> flowers = seasonalityService.getSeasonalFlowers(month);
      
      // then
      assertThat(flowers).contains("동백", "수선화");
  }
  ```

- [ ] **Step 2 (Green)** - Entity 및 Repository 생성
  ```java
  @Entity
  @Table(name = "seasonal_flowers")
  public class SeasonalFlower {
      @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
      
      @Column(nullable = false)
      private Integer month;
      
      @Column(nullable = false)
      private String flowerName;
      
      private String description;
      private Boolean peakSeason;
  }
  
  public interface SeasonalFlowerRepository extends JpaRepository<SeasonalFlower, Long> {
      List<SeasonalFlower> findByMonth(Integer month);
      List<SeasonalFlower> findByMonthAndPeakSeason(Integer month, Boolean peakSeason);
  }
  ```

- [ ] **Step 3 (Green)** - `SeasonalityService` 구현
  ```java
  @Service
  @RequiredArgsConstructor
  public class SeasonalityService {
      private final SeasonalFlowerRepository repository;
      
      public List<String> getSeasonalFlowers(int month) {
          return repository.findByMonth(month).stream()
              .map(SeasonalFlower::getFlowerName)
              .distinct()
              .collect(Collectors.toList());
      }
      
      public List<String> getCurrentSeasonalFlowers() {
          int currentMonth = LocalDate.now().getMonthValue();
          return getSeasonalFlowers(currentMonth);
      }
  }
  ```

- [ ] **Step 4 (Refactor)** - 성능 최적화
  - 캐싱 추가 (`@Cacheable("seasonal-flowers")`)
  - 12개월 모든 케이스 테스트 작성

**B. 시드 데이터 작성 (0.5일차)**
- [ ] `src/main/resources/data/seasonal_flowers.sql` 생성
  ```sql
  -- 1월: 동백, 수선화, 프리지아
  INSERT INTO seasonal_flowers (month, flower_name, description, peak_season) VALUES
  (1, '동백', '추운 겨울에도 피는 강인한 꽃', true),
  (1, '수선화', '겨울의 청초한 아름다움', true),
  
  -- 2월: 튤립, 프리지아
  (2, '튤립', '봄의 전령사', false),
  
  -- ... 12개월 데이터
  ```

---

#### 1.2 Flower Language DB (꽃말 데이터베이스)

**목표:** 상황별로 적절한 꽃말을 가진 꽃 추천

**작업 내용:**

**A. DB 설계 및 Enum (1일차)**
```java
public enum Occasion {
    CONFESSION("고백"),
    BIRTHDAY("생일"),
    ANNIVERSARY("기념일"),
    COMFORT("위로"),
    CONGRATULATION("축하/승진"),
    GRATITUDE("감사"),
    APOLOGY("사과"),
    GET_WELL("쾌유");
    
    private final String description;
}

public enum Emotion {
    LOVE("사랑"),
    RESPECT("존경"),
    SYMPATHY("동정/연민"),
    JOY("기쁨"),
    HOPE("희망");
}
```

```sql
CREATE TABLE flower_languages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    flower_name VARCHAR(100) NOT NULL,
    occasion VARCHAR(50) NOT NULL,          -- CONFESSION, COMFORT, etc.
    meaning TEXT NOT NULL,                  -- 꽃말 (예: "영원한 사랑")
    emotion VARCHAR(50),                    -- LOVE, RESPECT, etc.
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_occasion ON flower_languages(occasion);
CREATE INDEX idx_flower_name ON flower_languages(flower_name);
```

- [ ] **TDD: Step 1 (Red)** - `FlowerLanguageServiceTest` 작성
  ```java
  @Test
  @DisplayName("고백 상황에는 빨간 장미, 튤립이 추천되어야 한다")
  void should_recommendRoses_when_occasionIsConfession() {
      // given
      Occasion occasion = Occasion.CONFESSION;
      
      // when
      List<FlowerLanguageDto> flowers = flowerLanguageService.findByOccasion(occasion);
      
      // then
      assertThat(flowers).extracting("flowerName")
          .contains("빨간 장미", "빨간 튤립");
  }
  ```

- [ ] **Step 2 (Green)** - Entity, Repository, Service 구현
- [ ] **Step 3 (Refactor)** - 다중 상황 매칭 로직 추가

**B. 시드 데이터 작성 (0.5일차)**
```sql
INSERT INTO flower_languages (flower_name, occasion, meaning, emotion) VALUES
('빨간 장미', 'CONFESSION', '당신을 사랑합니다', 'LOVE'),
('빨간 장미', 'ANNIVERSARY', '영원한 사랑', 'LOVE'),
('흰 장미', 'APOLOGY', '순수한 마음', 'SYMPATHY'),
('노란 장미', 'GRATITUDE', '감사의 마음', 'JOY'),
('카네이션', 'GRATITUDE', '고마움', 'RESPECT'),
-- ... 20종 × 3상황 = 60개
```

---

#### 1.3 Sommelier Logic (소믈리에 추천 알고리즘)

**목표:** Who + Why + Vibe + Budget를 종합하여 최적의 꽃 추천

**작업 내용:**

**A. Request/Response DTO 설계 (0.5일차)**
```java
public record CurationRequest(
    @NotNull String who,                    // "연인", "부모님", "친구"
    @NotNull @Size(min = 1) List<String> why, // ["생일", "감사"]
    @NotNull Vibe vibe,                     // LOVELY, VIVID, CHIC, NATURAL
    @NotNull BigDecimal budget,             // 10000 ~ 200000
    String preferredColor                   // Optional
) {}

public record CurationResult(
    List<ProductDto> bestSeller,      // 리뷰 많은 순 Top 3
    List<ProductDto> storytelling,    // 꽃말 매칭도 높은 순 Top 3
    List<ProductDto> smartChoice,     // 가성비 높은 순 Top 3
    List<FlowerLanguageDto> flowerLanguages, // 추천 꽃말
    String recommendationReason       // 추천 근거 텍스트
) {}

public enum Vibe {
    LOVELY("사랑스러운", "#FFB6C1"),      // Light Pink
    VIVID("화사한", "#FF6B9D"),          // Hot Pink
    CHIC("세련된", "#2C2C54"),           // Dark Blue
    NATURAL("자연스러운", "#A8E6CF");    // Mint Green
    
    private final String description;
    private final String colorCode;
}
```

**B. Curation Service 구현 (2일차)**

- [ ] **TDD: Step 1 (Red)** - 복잡한 시나리오 테스트
  ```java
  @Test
  @DisplayName("연인에게 고백할 때 5만원 예산으로 LOVELY 분위기면 빨간 장미 추천")
  void should_recommendRedRoses_when_confessionToLover() {
      // given
      CurationRequest request = new CurationRequest(
          "연인",
          List.of("고백"),
          Vibe.LOVELY,
          BigDecimal.valueOf(50000),
          null
      );
      
      // when
      CurationResult result = curationService.recommend(request);
      
      // then
      assertThat(result.storytelling())
          .extracting("name")
          .anyMatch(name -> name.contains("장미"));
      assertThat(result.storytelling())
          .allMatch(p -> p.price().compareTo(request.budget()) <= 0);
  }
  ```

- [ ] **Step 2 (Green)** - 기본 구현
  ```java
  @Service
  @RequiredArgsConstructor
  public class CurationService {
      private final ProductRepository productRepository;
      private final FlowerLanguageService flowerLanguageService;
      private final SeasonalityService seasonalityService;
      
      @Transactional(readOnly = true)
      public CurationResult recommend(CurationRequest request) {
          // 1. 상황별 꽃말 조회
          List<String> recommendedFlowers = request.why().stream()
              .flatMap(occasion -> flowerLanguageService
                  .findByOccasion(Occasion.valueOf(occasion.toUpperCase()))
                  .stream()
                  .map(FlowerLanguageDto::flowerName))
              .distinct()
              .collect(Collectors.toList());
          
          // 2. 제철 꽃 필터링
          List<String> seasonalFlowers = seasonalityService.getCurrentSeasonalFlowers();
          
          // 3. QueryDSL로 상품 검색
          List<Product> products = searchProducts(
              recommendedFlowers, 
              seasonalFlowers, 
              request.vibe(), 
              request.budget()
          );
          
          // 4. 카테고리별 분류
          return categorizeProducts(products, request);
      }
      
      private List<Product> searchProducts(
          List<String> flowers, 
          List<String> seasonal,
          Vibe vibe, 
          BigDecimal budget
      ) {
          // QueryDSL 복합 검색
          QProduct p = QProduct.product;
          
          return queryFactory.selectFrom(p)
              .where(
                  p.isActive.isTrue(),
                  p.stockQuantity.gt(0),
                  p.price.loe(budget),
                  // 상품명에 추천 꽃 이름 포함
                  flowers.stream()
                      .map(p.name::containsIgnoreCase)
                      .reduce(BooleanExpression::or)
                      .orElse(null)
              )
              .fetch();
      }
      
      private CurationResult categorizeProducts(
          List<Product> products, 
          CurationRequest request
      ) {
          // Best Seller: 리뷰 많은 순
          List<ProductDto> bestSeller = products.stream()
              .sorted((a, b) -> b.getReviewCount().compareTo(a.getReviewCount()))
              .limit(3)
              .map(this::toDto)
              .toList();
          
          // Storytelling: 꽃말 매칭도 높은 순 (가중치 계산)
          List<ProductDto> storytelling = products.stream()
              .sorted((a, b) -> calculateScore(b, request).compareTo(calculateScore(a, request)))
              .limit(3)
              .map(this::toDto)
              .toList();
          
          // Smart Choice: 가성비 (가격 대비 평점)
          List<ProductDto> smartChoice = products.stream()
              .sorted((a, b) -> {
                  double scoreA = a.getAverageRating() / a.getPrice().doubleValue();
                  double scoreB = b.getAverageRating() / b.getPrice().doubleValue();
                  return Double.compare(scoreB, scoreA);
              })
              .limit(3)
              .map(this::toDto)
              .toList();
          
          return new CurationResult(
              bestSeller,
              storytelling,
              smartChoice,
              getFlowerLanguages(request.why()),
              generateReason(request)
          );
      }
      
      private Double calculateScore(Product product, CurationRequest request) {
          // 가중치 기반 점수 계산
          double score = 0.0;
          
          // 제철 꽃이면 +10점
          if (isSeasonalFlower(product)) score += 10;
          
          // 평점 가중치
          score += product.getAverageRating() * 5;
          
          // 리뷰 수 가중치
          score += Math.log(product.getReviewCount() + 1) * 2;
          
          // 예산 활용도 (예산의 70~100% 활용하면 보너스)
          double budgetRatio = product.getPrice().doubleValue() / request.budget().doubleValue();
          if (budgetRatio >= 0.7 && budgetRatio <= 1.0) {
              score += 5;
          }
          
          return score;
      }
  }
  ```

- [ ] **Step 3 (Refactor)** - 점수 계산 알고리즘 고도화
  - A/B 테스트용 점수 가중치 설정값 분리
  - 캐싱 전략 적용

**C. QueryDSL 설정 (0.5일차)**
- [ ] `build.gradle`에 QueryDSL 의존성 추가
- [ ] Q클래스 생성 확인
- [ ] `QuerydslConfig` 설정

---

### 🔴 2. AI Message Writer (AI 메시지 생성기)

#### 2.1 LLM API 연동 (2일차)

**A. OpenAI Client 구현**

- [ ] **의존성 추가**
  ```gradle
  implementation 'com.theokanning.openai-gpt3-java:service:0.18.2'
  ```

- [ ] **설정 파일**
  ```yaml
  # application.yml
  openai:
    api-key: ${OPENAI_API_KEY}
    model: gpt-4o-mini
    max-tokens: 200
    temperature: 0.7
    timeout: 10000
  ```

- [ ] **TDD: Step 1 (Red)** - `LlmClientTest` (Mock 사용)
  ```java
  @Test
  @DisplayName("LLM이 3개의 메시지를 생성해야 한다")
  void should_generate3Messages_when_called() {
      // given
      MessageRequest request = new MessageRequest(
          Occasion.CONFESSION,
          "여자친구",
          "빨간 장미",
          Tone.ROMANTIC
      );
      
      // when
      List<String> messages = llmClient.generateMessages(request);
      
      // then
      assertThat(messages).hasSize(3);
      assertThat(messages).allMatch(msg -> msg.length() > 10);
  }
  ```

- [ ] **Step 2 (Green)** - 인터페이스 및 구현
  ```java
  public interface LlmClient {
      List<String> generateMessages(MessageRequest request);
  }
  
  @Service
  @RequiredArgsConstructor
  public class OpenAiClient implements LlmClient {
      private final OpenAiService openAiService;
      
      @Override
      @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
      public List<String> generateMessages(MessageRequest request) {
          String prompt = buildPrompt(request);
          
          ChatCompletionRequest completion = ChatCompletionRequest.builder()
              .model("gpt-4o-mini")
              .messages(List.of(
                  new ChatMessage(ChatMessageRole.SYSTEM.value(), getSystemPrompt()),
                  new ChatMessage(ChatMessageRole.USER.value(), prompt)
              ))
              .temperature(0.7)
              .maxTokens(200)
              .build();
          
          ChatCompletionResult result = openAiService.createChatCompletion(completion);
          String response = result.getChoices().get(0).getMessage().getContent();
          
          return parseMessages(response);
      }
      
      private String buildPrompt(MessageRequest request) {
          return String.format("""
              상황: %s
              받는 사람: %s
              꽃: %s
              톤앤매너: %s
              
              위 정보를 바탕으로 감동적인 메시지를 3가지 길이로 작성해주세요:
              1. 짧은 버전 (10-15자)
              2. 중간 버전 (30-40자)
              3. 긴 버전 (60-80자)
              
              각 메시지는 줄바꿈으로 구분해주세요.
              """,
              request.occasion().getDescription(),
              request.recipient(),
              request.flowerName(),
              request.tone().getDescription()
          );
      }
  }
  ```

- [ ] **Step 3 (Refactor)** - 에러 핸들링 및 폴백
  - Timeout 처리
  - Rate Limit 초과 시 대기
  - 기본 메시지 폴백

**B. 비용 모니터링 (0.5일차)**
- [ ] API 호출 로깅
- [ ] 월별 사용량 추적 테이블
- [ ] 비용 알림 (월 10만원 초과 시)

---

#### 2.2 Prompt Engineering (1일차)

- [ ] **시스템 프롬프트 설계**
  ```java
  private String getSystemPrompt() {
      return """
          당신은 20년 경력의 플로리스트이자 감성적인 문구 작가입니다.
          고객의 진심을 꽃과 함께 전달하는 메시지를 작성해주세요.
          
          규칙:
          1. 진부한 표현 지양 (예: "언제나 응원해", "파이팅" 등)
          2. 구체적이고 감성적인 표현 사용
          3. 받는 사람의 입장에서 감동받을 만한 내용
          4. 욕설, 비속어, 부적절한 표현 금지
          5. 각 버전은 명확히 다른 길이와 뉘앙스
          """;
  }
  ```

- [ ] **톤앤매너별 예시 테스트**
  ```java
  @Test
  void should_generateFormalTone_when_toneIsFormal() {
      // FORMAL: "진심으로 감사드립니다", "존경하는 마음"
  }
  
  @Test
  void should_generateCasualTone_when_toneIsCasual() {
      // CASUAL: "고마워", "늘 함께해줘서 좋아"
  }
  
  @Test
  void should_generateRomanticTone_when_toneIsRomantic() {
      // ROMANTIC: "당신과의 모든 순간이 특별해요", "영원히 사랑해"
  }
  ```

- [ ] **부적절 표현 필터**
  - 욕설 필터 리스트
  - 정치/종교 관련 키워드 차단

---

#### 2.3 Controller 및 API (0.5일차)

- [ ] **CurationController 생성**
  ```java
  @RestController
  @RequestMapping("/api/v1/curation")
  @RequiredArgsConstructor
  public class CurationController {
      private final CurationService curationService;
      private final MessageGeneratorService messageGeneratorService;
      
      @PostMapping("/recommend")
      @Operation(summary = "AI 꽃 추천", description = "상황에 맞는 꽃을 AI가 추천합니다")
      public ResponseEntity<CurationResult> recommend(
          @Valid @RequestBody CurationRequest request
      ) {
          CurationResult result = curationService.recommend(request);
          return ResponseEntity.ok(result);
      }
      
      @PostMapping("/message")
      @Operation(summary = "AI 메시지 생성", description = "감동적인 메시지 3종을 생성합니다")
      public ResponseEntity<List<String>> generateMessage(
          @Valid @RequestBody MessageRequest request
      ) {
          List<String> messages = messageGeneratorService.generateMessages(request);
          return ResponseEntity.ok(messages);
      }
  }
  ```

- [ ] **TDD: Controller Test**
  ```java
  @WebMvcTest(CurationController.class)
  class CurationControllerTest {
      @Test
      @DisplayName("추천 API는 200 OK를 반환해야 한다")
      void should_return200_when_recommend() throws Exception {
          // given
          CurationRequest request = new CurationRequest(...);
          given(curationService.recommend(any())).willReturn(...);
          
          // when & then
          mockMvc.perform(post("/api/v1/curation/recommend")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.bestSeller").isArray());
      }
  }
  ```

---

## 📅 일정 계획 (14일)

| 주차 | 일정 | 작업 | 담당 | 완료 |
|------|------|------|------|------|
| **Week 1** | Day 1-2 | Seasonality + Flower Language DB | Backend | [ ] |
| | Day 3-5 | Sommelier Logic (QueryDSL) | Backend | [ ] |
| **Week 2** | Day 6-7 | LLM API 연동 | Backend | [ ] |
| | Day 8 | Prompt Engineering | Backend | [ ] |
| | Day 9-10 | Controller & API 테스트 | Backend | [ ] |
| **Week 3** | Day 11-12 | 시드 데이터 작성 & 성능 최적화 | Backend | [ ] |
| | Day 13-14 | 통합 테스트 & 버그 수정 | Full Stack | [ ] |

---

## ✅ 각 작업 완료 조건 (Definition of Done)

1. 🔴 **Red**: 실패하는 테스트 먼저 작성
2. 🟢 **Green**: 테스트를 통과하는 최소 코드 작성
3. 🔵 **Refactor**: 코드 개선 (테스트는 계속 Green)
4. ✅ **모든 테스트 통과** (단위 + 통합)
5. ✅ **코드 커버리지 80% 이상**
6. ✅ **Swagger 문서 업데이트**
7. ✅ **코드 리뷰 승인**
8. ✅ **Git Commit (한글 메시지)**

---

## 🚀 시작 체크리스트

시작 전 반드시 확인:
- [ ] OpenAI API Key 발급 완료
- [ ] 제철 꽃 데이터 수집 (12개월 × 5종)
- [ ] 꽃말 데이터 수집 (20종 × 3상황)
- [ ] QueryDSL 의존성 추가
- [ ] TDD_GUIDELINES.md 숙지
- [ ] Phase 1 테스트 전부 통과 확인

---

## 📞 문의 및 지원

- **기술 이슈:** GitHub Issues 등록
- **API 비용 문제:** 비용 최적화 논의
- **일정 조정:** PM과 협의

**🍷 Flori Sommelier, 시작합니다!**
