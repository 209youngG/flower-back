# Phase 2 진행 상황 체크포인트

**생성일**: 2026-01-29  
**세션**: Sisyphus - Phase 2 Backend Development  
**프로젝트**: flower-back (Spring Boot 4.0 + Java 25)

---

## ✅ 완료된 작업 (2/4)

### 1. Seasonality Check 시스템 ✅
- **커밋**: `eca6970` - "feat: Seasonality Check 시스템 구현 (TDD)"
- **테스트**: 7/7 passed
- **파일**:
  - Entity: `SeasonalFlower.java`
  - Repository: `SeasonalFlowerRepository.java`
  - Service: `SeasonalityService.java`
  - Test: `SeasonalityServiceTest.java`
  - Seed Data: `seasonal_flowers.sql` (60 entries)
- **기능**:
  - 월별 제철 꽃 조회 (1~12월)
  - 현재 월 제철 꽃 조회
  - 성수기 꽃 필터링
  - 입력 검증 (1~12 범위)

### 2. Flower Language DB 시스템 ✅
- **커밋**: `23246e8` - "feat: Flower Language DB 시스템 구현 (TDD)"
- **테스트**: 5/5 passed
- **파일**:
  - Enums: `Occasion.java` (8 values), `Emotion.java` (5 values)
  - Entity: `FlowerLanguage.java`
  - Repository: `FlowerLanguageRepository.java`
  - Service: `FlowerLanguageService.java`
  - DTO: `FlowerLanguageDto.java`
  - Test: `FlowerLanguageServiceTest.java`
  - Seed Data: `flower_languages.sql` (51 entries)
- **기능**:
  - 상황별 꽃 추천 (고백, 생일, 기념일, 위로, 축하, 감사, 사과, 쾌유)
  - 꽃 이름으로 꽃말 조회
  - 상황+감정 복합 필터링

---

## 📊 누적 통계

| 항목 | 수치 |
|------|------|
| **총 테스트** | 12개 (100% 통과) |
| **Entity** | 2개 (SeasonalFlower, FlowerLanguage) |
| **Service** | 2개 (SeasonalityService, FlowerLanguageService) |
| **Repository** | 2개 |
| **Enum** | 2개 (Occasion 8종, Emotion 5종) |
| **시드 데이터** | 111개 (Seasonal 60 + Language 51) |
| **커밋** | 2개 |
| **작성 코드** | ~700 lines |

---

## 🚀 다음 작업: Sommelier Logic (추천 알고리즘)

### 작업 개요
**목표**: Who + Why + Vibe + Budget를 종합하여 최적의 꽃 추천

### 상세 작업 목록

#### A. Enum 및 DTO 설계 (0.5일)
- [ ] `Vibe` Enum 작성
  ```java
  public enum Vibe {
      LOVELY("사랑스러운", "#FFB6C1"),      // 핑크 계열
      VIVID("생동감 있는", "#FF6347"),      // 레드/오렌지 계열
      CHIC("세련된", "#2F4F4F"),           // 다크 그린/블랙 계열
      NATURAL("자연스러운", "#90EE90");     // 그린/화이트 계열
  }
  ```

- [ ] Request DTO 작성: `CurationRequest.java`
  ```java
  public record CurationRequest(
      @NotNull String who,                    // "연인", "부모님", "친구"
      @NotNull @Size(min = 1) List<String> why, // ["생일", "감사"]
      @NotNull Vibe vibe,
      @NotNull BigDecimal budget,
      String preferredColor                   // Optional
  ) {}
  ```

- [ ] Response DTO 작성: `CurationResult.java`
  ```java
  public record CurationResult(
      List<ProductDto> bestSeller,      // 리뷰 많은 순 Top 3
      List<ProductDto> storytelling,    // 꽃말 매칭도 높은 순 Top 3
      List<ProductDto> smartChoice,     // 가성비 높은 순 Top 3
      List<FlowerLanguageDto> flowerLanguages,
      String recommendationReason
  ) {}
  ```

#### B. Service 로직 구현 (1일)
- [ ] `CurationServiceTest.java` 작성 (TDD Red)
  - 테스트 케이스:
    1. "연인 + 고백 + LOVELY + 5만원" → 빨간 장미 포함 추천
    2. "부모님 + 감사 + NATURAL + 3만원" → 카네이션 포함 추천
    3. "친구 + 생일 + VIVID + 10만원" → 화려한 꽃다발 추천

- [ ] `CurationService.java` 구현 (TDD Green)
  ```java
  public CurationResult recommendFlowers(CurationRequest request) {
      // 1. Occasion 매핑 (why → List<Occasion>)
      // 2. Seasonality 필터 (현재 월 제철 꽃만)
      // 3. FlowerLanguage 필터 (Occasion 매칭)
      // 4. Product 조회 및 스코어링
      // 5. 3가지 카테고리 분류 (Best/Story/Smart)
  }
  ```

#### C. 스코어링 알고리즘 (0.5일)
- [ ] 가중치 설정
  ```
  - 꽃말 매칭도: 40%
  - 제철 여부: 20%
  - Vibe 색상 매칭: 20%
  - 예산 적합도: 10%
  - 리뷰 수: 10%
  ```

- [ ] 카테고리별 정렬
  - **Best Seller**: `ORDER BY reviewCount DESC LIMIT 3`
  - **Storytelling**: `ORDER BY flowerLanguageScore DESC LIMIT 3`
  - **Smart Choice**: `ORDER BY (price / quality_score) ASC LIMIT 3`

#### D. 통합 테스트 (0.5일)
- [ ] End-to-End 시나리오 테스트
- [ ] Edge Case 처리
  - 예산 부족 시 처리
  - 제철 꽃 없을 때 처리
  - 매칭되는 꽃말 없을 때 처리

---

## 🔧 기술적 고려사항

### QueryDSL 도입 필요성
현재 `JpaRepository`만으로는 복잡한 조회 쿼리 작성이 어렵습니다.

**Option 1: QueryDSL 추가 (권장)**
```gradle
// curation/build.gradle
dependencies {
    implementation 'com.querydsl:querydsl-jpa:5.1.0:jakarta'
    annotationProcessor 'com.querydsl:querydsl-apt:5.1.0:jakarta'
    annotationProcessor 'jakarta.persistence:jakarta.persistence-api'
}
```

**Option 2: JPQL with @Query**
- 복잡도가 낮으면 `@Query` 사용 가능
- 하지만 동적 쿼리 작성이 어려움

### Product 모듈 의존성
- `curation` 모듈이 `product` 모듈의 Entity를 참조해야 함
- `build.gradle`에 의존성 추가 필요:
  ```gradle
  dependencies {
      implementation project(':product')
  }
  ```

---

## 📝 다음 세션 시작 명령어

```bash
# 1. 프로젝트 디렉토리로 이동
cd /Users/iyeong-gyun/IdeaProjects/flower-back

# 2. Git 상태 확인
git status
git log --oneline -3

# 3. 최신 커밋 확인
# eca6970 - Seasonality Check
# 23246e8 - Flower Language DB

# 4. 테스트 실행 확인
./gradlew :curation:test

# 5. Phase 2 작업 계획 확인
cat PHASE2_TASKS.md | grep -A 50 "1.3 Sommelier Logic"
```

### 다음 세션에서 할 말
> "Phase 2 Sommelier Logic 계속 진행해줘"

또는

> "Continue"

---

## ⚠️ 알려진 이슈

1. **LSP Gradle 버전 경고** (무시 가능)
   - `Spring Boot plugin requires Gradle 8.x (8.14 or later)`
   - 실제 빌드에는 영향 없음 (Gradle 9.2.1 사용 중)

2. **H2 예약어 이슈** (해결됨)
   - `month` 컬럼 → `"MONTH"` 백틱 처리 완료

3. **created_at NULL 이슈** (해결됨)
   - SQL INSERT에서 제외, Entity default 값 사용

---

## 📦 현재 모듈 구조

```
flower-back/
├── curation/               # 🟢 진행 중
│   ├── domain/
│   │   ├── SeasonalFlower.java ✅
│   │   └── FlowerLanguage.java ✅
│   ├── repository/
│   │   ├── SeasonalFlowerRepository.java ✅
│   │   └── FlowerLanguageRepository.java ✅
│   ├── service/
│   │   ├── SeasonalityService.java ✅
│   │   └── FlowerLanguageService.java ✅
│   ├── enums/
│   │   ├── Occasion.java ✅
│   │   └── Emotion.java ✅
│   ├── dto/
│   │   └── FlowerLanguageDto.java ✅
│   └── resources/
│       ├── seasonal_flowers.sql ✅
│       └── flower_languages.sql ✅
├── product/                # 참조 필요
├── common/                 # 공통 커널
└── api/                    # 진입점 (나중에 Controller 추가)
```

---

## 🎯 Phase 2 전체 로드맵

| 단계 | 기능 | 상태 | 예상 시간 |
|------|------|------|-----------|
| 1.1 | Seasonality Check | ✅ | 1일 |
| 1.2 | Flower Language DB | ✅ | 1일 |
| 1.3 | Sommelier Logic | ⬜ | 2일 |
| 2.1 | AI Message Writer | ⬜ | 1일 |
| 2.2 | API Controller | ⬜ | 0.5일 |
| 2.3 | Integration Test | ⬜ | 0.5일 |
| **Total** | | **33%** | **6일** |

---

**현재 진행률**: 2/6 단계 완료 (33%)  
**남은 작업**: Sommelier Logic, AI Message Writer, API 통합  
**다음 목표**: Sommelier Logic TDD 시작

---

_이 파일은 자동 생성되었습니다. 다음 세션에서 "Continue" 명령으로 작업을 재개할 수 있습니다._
