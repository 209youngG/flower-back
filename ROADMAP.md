# Flori Development Roadmap 🌹

**Goal:** 누구나 쉬운 꽃 선물 플랫폼, '플로리(Flori)' 구축
**Vision:** "번역" - 플로리스트의 언어를 소비자의 감성 언어로.

---

## Phase 0: Architecture & Foundation 🏗️
- [x] **Multi-Module Refactoring**
  - [x] `store` 모듈 생성 (파트너/꽃집 관리)
  - [x] `curation` 모듈 생성 (AI 소믈리에 엔진)
  - [x] `auction` 모듈 생성 (오늘의 꽃/타임세일)
- [x] **Role Expansion** (`member` module)
  - [x] User Role 추가: `ROLE_SELLER` (꽃집 사장님)
  - [x] Admin 권한 분리: `ROOT_ADMIN` vs `STORE_ADMIN`

## Phase 1: O2O Platform Base (Seller & Store) 🏪
**목표:** 꽃집 사장님이 입점하고 상품을 등록할 수 있는 기반 마련

### 1.1 Store Management
- [ ] Store Entity 설계 (상호명, 위치(Lat/Lon), 영업시간, 휴무일)
- [ ] Store 등록/승인 API 구현 (`admin` 승인 프로세스)
- [ ] 내 주변 꽃집 검색 API (Spatial Index or Redis Geo)

### 1.2 Product Expansion (Multi-Tenancy)
- [ ] Product Entity에 `storeId` 연관관계 매핑
- [ ] 표준 사이즈 규격(S, M, L, XL) Enum 및 가이드라인 로직 구현
- [ ] 사장님용 상품 CRUD API (본인 가게 상품만 관리)

## Phase 2: Flori Sommelier (AI Curation) 🍷
**목표:** 상황과 예산에 맞는 꽃 추천 및 메시지 생성

### 2.1 Curation Engine
- [ ] **Seasonality Check**: 월별 제철 꽃 데이터베이스 구축 및 필터링
- [ ] **Flower Language DB**: 상황(고백, 위로, 승진)별 꽃말 매핑
- [ ] **Sommelier Logic**: Who + Why + Vibe + Budget -> Product QueryDSL 검색 구현

### 2.2 AI Message Writer
- [ ] LLM (OpenAI/Claude) API 연동 Client 구현
- [ ] Prompt Engineering: 상황/꽃/톤앤매너 기반 메시지 3종 생성
- [ ] 메시지 생성 API (`/api/v1/curation/message`)

## Phase 3: Trust & Time Sale 🤝
**목표:** 실물 신뢰도 확보 및 악성 재고 처리

### 3.1 Trust Preview (안심 프리뷰)
- [ ] Order Status 확장: `PREPARING` -> `PREVIEW_SENT` -> `DELIVERING`
- [ ] 프리뷰 이미지 업로드 및 Push Notification 발송 로직
- [ ] 이미지 리사이징/최적화 처리 (S3/CDN)

### 3.2 Time Sale (오늘의 꽃)
- [ ] `auction` 모듈: 유효시간(Time-to-Live)이 있는 상품 등록
- [ ] 재고 소진 시 자동 마감 스케줄러/로직
- [ ] 위치 기반 '내 주변 마감 임박 꽃' 조회 API

## Phase 4: Growth & Expansion 🚀
- [ ] **Visual Search**: 이미지 업로드 -> 유사 색감/형태 상품 검색 (Vector DB)
- [ ] **Subscription**: B2B 정기 구독 결제 및 스케줄링 (`batch` 모듈)
- [ ] **Review Challenge**: 포토 리뷰 및 비교 인증 기능

---
**Note:** 이 로드맵은 애자일하게 변경될 수 있으며, 각 Phase 완료 시 반드시 테스트 코드를 동반해야 합니다.
