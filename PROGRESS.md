# 🌹 Flower-Back 개발 진행 상황

**최종 업데이트:** 2026-01-29  
**현재 Phase:** Phase 2 (Flori Sommelier - AI Curation) 진행 중  
**진행률:** 2/6 작업 완료 (33%)

---

## 📊 현재 상태 요약

### ✅ 완료된 작업
1. **Phase 0: Architecture & Foundation** ✅
   - Multi-Module Refactoring (`store`, `curation`, `auction` 모듈 생성)
   - Role Expansion (`ROLE_SELLER`, Admin 권한 분리)

2. **Phase 1: O2O Platform Base** ✅
   - Store Management (상점 등록/조회 API)
   - Product Expansion (Multi-Tenancy, 상품 CRUD)

3. **Phase 2: Flori Sommelier** (진행 중 🚧)
   - ✅ **1.1 Seasonality Check** (제철 꽃 필터링)
     - `SeasonalFlower` Entity, Repository, Service 구현
     - TDD 완료 (테스트 100% 통과)
   - ✅ **1.2 Flower Language DB** (꽃말 데이터베이스)
     - `FlowerLanguage` Entity, Repository, Service 구현
     - Enum (`Occasion`, `Emotion`) 정의
     - TDD 완료 (테스트 100% 통과)
   - 🚧 **1.3 Sommelier Logic** (추천 알고리즘)
     - CurationRequest/Result DTO 설계 완료
     - Vibe Enum 정의 완료
     - CurationServiceTest 작성 완료 (TDD Red 단계)
     - ❌ **CurationService 구현 필요** (다음 작업)

### 🚧 현재 작업 중
- **CurationService 구현** (TDD Green 단계)
  - Who + Why + Vibe + Budget → Product 추천 로직
  - QueryDSL 복합 검색 구현
  - 카테고리별 분류 (bestSeller, storytelling, smartChoice)

---

## 🎯 다음 작업 체크리스트

### 1. CurationService 구현 (우선순위: 🔴 High)
- [ ] `CurationService.java` 생성 및 기본 골격 작성
- [ ] `recommendFlowers()` 메서드 구현
  - [ ] 꽃말 기반 추천 꽃 리스트 조회
  - [ ] 제철 꽃 필터링 적용
  - [ ] QueryDSL로 Product 검색 (예산, 재고, 활성화 상태)
  - [ ] 카테고리별 분류 로직 (bestSeller, storytelling, smartChoice)
  - [ ] 점수 계산 알고리즘 (`calculateScore()`)
  - [ ] 추천 이유 생성 로직 (`generateReason()`)
- [ ] FlowerLanguageDto 생성 (아직 없음)
- [ ] 테스트 실행 및 통과 확인 (`./gradlew :curation:test`)
- [ ] 코드 리팩토링 (TDD Refactor 단계)

### 2. QueryDSL 설정 (우선순위: 🟡 Medium)
- [ ] `curation/build.gradle`에 QueryDSL 의존성 추가
- [ ] Q클래스 생성 확인 (`./gradlew :curation:compileQuerydsl`)
- [ ] `QuerydslConfig` 설정 (JPAQueryFactory Bean 등록)

### 3. AI Message Writer (우선순위: 🟢 Low)
- [ ] OpenAI API Client 구현
- [ ] Prompt Engineering
- [ ] MessageRequest/Response DTO 설계
- [ ] `MessageGeneratorService` 구현

### 4. Controller 및 API (우선순위: 🟡 Medium)
- [ ] `CurationController` 생성 (`api` 모듈)
- [ ] `/api/v1/curation/recommend` API 구현
- [ ] `/api/v1/curation/message` API 구현
- [ ] Swagger 문서 작성 (`@Operation`)

### 5. 시드 데이터 작성
- [ ] 제철 꽃 데이터 (12개월 × 5종) 추가
- [ ] 꽃말 데이터 (20종 × 3상황) 추가

---

## 🏗️ 주요 아키텍처 결정사항

### 1. **모듈 구조**
- `curation` 모듈: AI 소믈리에 엔진 (추천 로직)
- `api` 모듈: Controller (REST API Entry Point)
- `product` 모듈: 상품 도메인 (QueryDSL 검색)

### 2. **의존성 방향**
```
api → curation → product
api → curation → common
```

### 3. **TDD 원칙 준수**
- 🔴 **Red**: 실패하는 테스트 먼저 작성
- 🟢 **Green**: 최소한의 구현으로 테스트 통과
- 🔵 **Refactor**: 코드 개선 (테스트는 계속 통과)

### 4. **DTO 설계 (Java 25 Records 사용)**
```java
// Request
public record CurationRequest(
    @NotNull String who,
    @NotNull @Size(min = 1) List<String> why,
    @NotNull Vibe vibe,
    @NotNull BigDecimal budget,
    String preferredColor
) {}

// Response
public record CurationResult(
    List<ProductDto> bestSeller,
    List<ProductDto> storytelling,
    List<ProductDto> smartChoice,
    List<FlowerLanguageDto> flowerLanguages,
    String recommendationReason
) {}

// Enum
public enum Vibe {
    LOVELY, VIVID, CHIC, NATURAL
}
```

### 5. **점수 계산 알고리즘 (가중치 기반)**
- 제철 꽃: +10점
- 평점: ×5
- 리뷰 수: log(n+1) ×2
- 예산 활용도: 70~100% 활용 시 +5점

---

## 📝 Git 커밋 메시지 히스토리 (최근 5개)

```
48ee9ac docs: Phase 2 진행 상황 체크포인트 (2/6 완료)
23246e8 feat: Flower Language DB 시스템 구현 (TDD)
eca6970 feat: Seasonality Check 시스템 구현 (TDD)
5a34247 docs: Phase 2 백엔드 작업 계획서 작성
45f3fc1 feat: Phase 1 O2O Platform Base 완료
```

---

## 🚀 다음 세션에서 시작하는 방법

### **한 마디 명령어:**
```
"CurationService 구현 계속"
```

또는:

```
"PROGRESS.md 확인하고 다음 작업 시작"
```

### **상세 시작 절차:**
1. `PROGRESS.md` 파일 읽기
2. "다음 작업 체크리스트" 섹션 확인
3. 우선순위 🔴 High 작업부터 시작
4. TDD Red → Green → Refactor 사이클 반복

---

## 📂 현재 파일 상태 (Untracked/Modified)

### Modified:
- `curation/build.gradle`

### Untracked (커밋 필요):
- `curation/src/main/java/com/flower/curation/dto/CurationRequest.java`
- `curation/src/main/java/com/flower/curation/dto/CurationResult.java`
- `curation/src/main/java/com/flower/curation/enums/Vibe.java`
- `curation/src/test/java/com/flower/curation/service/CurationServiceTest.java`

---

## 📌 중요 참고 문서

- `AGENTS.md`: 개발 프로토콜 및 아키텍처 가이드
- `ROADMAP.md`: 전체 Phase별 로드맵
- `PHASE2_TASKS.md`: Phase 2 상세 작업 계획서
- `README.md`: 프로젝트 소개 및 실행 방법

---

## 🛠️ 기술 스택

- **Java**: 25 (Preview Features)
- **Framework**: Spring Boot 4.0.2
- **Build**: Gradle 9.2.1
- **ORM**: JPA/Hibernate
- **Testing**: JUnit 5, Mockito, AssertJ
- **Architecture**: Modular Monolith (Event-Driven)

---

**마지막 작업자:** Sisyphus (Antigravity Agent)  
**다음 작업 예상 시간:** 2-3시간 (CurationService 구현 완료까지)
