# Flower Shop Backend (flower-back) (Modular Monolith)

Spring Boot 4.0.0과 Java 25를 기반으로 한 꽃 쇼핑몰 백엔드 프로젝트입니다.
도메인 주도 설계(DDD)와 이벤트 기반 아키텍처(Event-Driven Architecture)를 적용하여 확장성과 유지보수성을 극대화한 **모듈러 모놀리스(Modular Monolith)** 구조를 따르고 있습니다.

## 🏗 아키텍처 및 특징

### 1. 멀티 모듈 구조 (Multi-Module)
비즈니스 도메인별로 물리적인 모듈을 분리하여 의존성을 관리합니다.
- **`api`**: 클라이언트 요청을 처리하는 진입점 (Controller, DTO, Swagger)
- **`order`**: 주문 도메인 (주문 생성, 취소, 결제 대기)
- **`product`**: 상품 도메인 (상품 관리, 재고 관리, 입고)
- **`inventory`**: 재고 도메인 (주문 시 재고 차감 리스너)
- **`delivery`**: 배송 도메인 (배송 생성 및 상태 관리)
- **`cart`**: 장바구니 도메인 (상품 담기, 수량 조절)
- **`member`**: 회원 도메인 (가입, 로그인, 포인트)
- **`batch`**: 배치 도메인 (실패한 트랜잭션 재처리, 스케줄러)
- **`common`**: 공통 커널 (이벤트 객체, 예외, 공통 엔티티)

### 2. 이벤트 기반 아키텍처 (Event-Driven & Consistency)
모듈 간의 강한 결합을 피하고 데이터 정합성을 보장하기 위해 Spring Event를 활용합니다.

- **비동기 처리**: 주문 완료(`OrderPlacedEvent`) 시 `inventory`(재고 차감)와 `delivery`(배송 생성) 모듈이 비동기로 반응합니다.
- **보상 트랜잭션 (Compensation Transaction)**:
    - 재고 차감 실패 시 `InventoryDeductionFailedEvent`를 발행하여 주문을 자동으로 취소합니다.
    - 이를 통해 분산 환경(논리적 분리)에서도 데이터 일관성을 유지합니다.
- **회복 탄력성 (Resilience)**:
    - 자동 취소마저 실패할 경우, `FailureLog` 테이블에 기록합니다(DLQ 패턴).
    - `batch` 모듈의 스케줄러가 주기적으로 실패 로그를 조회하여 재시도(Retry)를 수행합니다.

### 3. SOLID 원칙 준수 (설계 철학)
객체지향 설계 원칙을 준수하여 유지보수하기 쉬운 코드를 지향합니다.
- **SRP (단일 책임 원칙)**: 각 모듈과 리스너는 하나의 명확한 책임(주문, 재고, 배송, 재시도 등)만 가집니다.
- **OCP (개방 폐쇄 원칙)**: 
    - `batch` 모듈의 재시도 로직은 **전략 패턴(Strategy Pattern)**을 사용하여 구현되었습니다.
    - 새로운 도메인의 재시도 로직이 추가되더라도 스케줄러 코드를 수정할 필요 없이 `RetryStrategy` 구현체만 추가하면 됩니다.
- **DIP (의존 역전 원칙)**: 모듈 간 통신은 구체적인 클래스가 아닌 추상화된 `Event`와 인터페이스를 통해 이루어집니다.

### 4. 최신 기술 스택
- **Java**: 25 (최신 LTS 기능 활용)
- **Framework**: Spring Boot 4.0.0
- **Build**: Gradle 9.2.1 (Groovy DSL)
- **Database**: H2 (In-memory, 테스트용), JPA/Hibernate

## 📦 주요 기능

### 회원 관리
- **회원 가입/로그인**: 이메일 기반 인증 및 비밀번호 암호화 저장.
- **포인트**: 가입 시 기본 등급 부여 및 포인트 적립/사용.

### 주문 프로세스
1. **주문 생성**: 사용자가 주문을 요청하면 `Order` 엔티티가 생성되고 `OrderPlacedEvent`가 발행됩니다.
2. **재고 차감**: `inventory` 모듈이 이벤트를 수신하여 `ProductService`를 통해 재고를 차감합니다.
3. **배송 생성**: `delivery` 모듈이 이벤트를 수신하여 배송 정보를 생성합니다.
4. **예외 처리**: 재고 부족 등의 사유로 차감 실패 시, 주문은 자동으로 **취소(CANCELLED)** 처리됩니다.

### 재고 관리
- **재고 차감**: 주문 시 원자적으로 차감됩니다.
- **재고 입고**: `/api/v1/products/{id}/restock` API를 통해 상품 재고를 증가시킬 수 있습니다.

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
#### 1. API 서버 실행
```bash
./gradlew :api:bootRun
```
- Swagger UI: `http://localhost:8080/swagger-ui/index.html` (설정 시)
- 기본 포트: 8080

#### 2. Batch 서버 실행 (재처리 스케줄러)
```bash
./gradlew :batch:bootRun
```
- 실패한 보상 트랜잭션을 1분 주기로 재시도합니다.

## 📁 디렉토리 구조
```
flower/
├── api/           # Presentation Layer (Web, Swagger)
├── batch/         # Batch Jobs (Retry Scheduler)
├── cart/          # Cart Domain
├── common/        # Shared Kernel (Events, Entity, Utils)
├── delivery/      # Delivery Domain
├── inventory/     # Inventory Logic (Listeners)
├── member/        # Member Domain
├── order/         # Order Domain
├── product/       # Product Domain
├── build.gradle   # Root Build Script
└── settings.gradle # Module Inclusion
```
