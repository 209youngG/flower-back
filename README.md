# Flower Shop Backend (flower-back) (Modular Monolith)

Spring Boot 4.0.0과 Java 25를 기반으로 한 꽃 쇼핑몰 백엔드 프로젝트입니다.
도메인 주도 설계(DDD)와 이벤트 기반 아키텍처(Event-Driven Architecture)를 적용하여 확장성과 유지보수성을 극대화한 **모듈러 모놀리스(Modular Monolith)** 구조를 따르고 있습니다.

## 🏗 아키텍처 및 특징

### 1. 멀티 모듈 구조 (Multi-Module)
비즈니스 도메인별로 물리적인 모듈을 분리하여 의존성을 관리합니다.
- **`api`**: 클라이언트 요청을 처리하는 진입점 (Controller, DTO)
- **`cart`**: 장바구니 도메인 (상품 담기, 수량 조절)
- **`order`**: 주문 도메인 (주문 생성, 결제 준비)
- **`product`**: 상품 도메인 (상품 정보, 재고 관리)
- **`member`**: 회원 도메인 (가입, 로그인, 포인트)
- **`delivery`**: 배송 도메인 (배송 상태 관리)
- **`inventory`**: 재고 도메인 (재고 차감 및 복구)
- **`common`**: 공통 이벤트 객체 및 유틸리티

### 2. 이벤트 기반 아키텍처 (Event-Driven)
모듈 간의 강한 결합을 피하기 위해 Spring Event를 사용합니다.
- **트랜잭션 분리**: `@TransactionalEventListener(phase = AFTER_COMMIT)`을 사용하여 주문 트랜잭션이 성공한 후에만 후속 작업(재고 차감, 배송 요청)이 실행됩니다.
- **확장성**: 새로운 기능(예: 알림) 추가 시 기존 로직 수정 없이 리스너만 추가하면 됩니다.

### 3. 최신 기술 스택
- **Java**: 25 (최신 LTS 기능 활용)
- **Framework**: Spring Boot 4.0.0
- **Build**: Gradle 9.2.1 (Groovy DSL)
- **Database**: H2 (In-memory, 테스트용), JPA/Hibernate

## 🚀 실행 방법

### 요구 사항
- JDK 25 이상
- Gradle 9.2.1 이상 (Wrapper 포함됨)

### 빌드 및 테스트
```bash
# 전체 프로젝트 빌드 및 테스트 실행
./gradlew clean build
```

### 애플리케이션 실행
```bash
# API 모듈 실행 (Spring Boot 앱 시작)
./gradlew :api:bootRun
```
서버는 기본적으로 `http://localhost:8080`에서 실행됩니다.

## 📁 주요 디렉토리 구조
```
flower/
├── api/           # Presentation Layer (Controller)
├── cart/          # Cart Domain
├── order/         # Order Domain
├── product/       # Product Domain
├── member/        # Member Domain
├── common/        # Shared Kernel (Events, Exceptions)
├── build.gradle   # Root Build Script
└── settings.gradle # Module Inclusion
```
