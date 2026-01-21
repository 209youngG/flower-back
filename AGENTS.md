# flower-back을 위한 에이전트 코딩 가이드라인

이 문서는 이 리포지토리에서 작업하는 AI 에이전트를 위한 프로토콜, 명령어 및 스타일 가이드를 정의합니다.

## 1. 환경 및 빌드 (Environment & Build)

### 1.1 기술 스택 (Tech Stack)
- **언어**: Java 25 (최신 기능 사용)
- **프레임워크**: Spring Boot 4.0.0
- **빌드 시스템**: Gradle 9.2.1 (Groovy DSL)
- **데이터베이스**: H2 (로컬/테스트용 인메모리), JPA/Hibernate
- **아키텍처**: 모듈러 모놀리스 (Modular Monolith), 이벤트 기반 (Event-Driven)

### 1.2 명령어 (Commands)
모든 명령어는 프로젝트 루트(`/Users/iyeong-gyun/IdeaProjects/flower-back`)에서 실행해야 합니다.

| 동작 | 명령어 | 비고 |
|--------|---------|-------|
| **프로젝트 빌드** | `./gradlew clean build` | 모든 모듈을 빌드합니다 |
| **API 서버 실행** | `./gradlew :api:bootRun` | 8080 포트에서 실행됩니다 |
| **배치 서버 실행** | `./gradlew :batch:bootRun` | 백그라운드 작업을 실행합니다 |
| **전체 테스트 실행** | `./gradlew test` | 모든 모듈의 테스트를 실행합니다 |
| **모듈 테스트 실행** | `./gradlew :<module>:test` | 예: `./gradlew :member:test` |
| **단일 테스트 실행** | `./gradlew :<module>:test --tests "com.flower.<pkg>.<Class>"` | **검증 시 권장됨** |

**참고**: 항상 Gradle Wrapper (`./gradlew`)를 사용하십시오.

## 2. 아키텍처 및 구조 (Architecture & Structure)

이 프로젝트는 **모듈러 모놀리스(Modular Monolith)** 구조입니다. 코드는 계층(layer)이 아닌 비즈니스 도메인별로 구성됩니다.

### 2.1 모듈 (Modules)
- `api`: 프레젠테이션 계층 (Controllers, Swagger). **진입점(Entry Point)**.
- `member`, `order`, `product`: 도메인 모듈 (핵심 비즈니스 로직).
- `inventory`, `delivery`: 지원 도메인.
- `common`: 공통 커널 (이벤트, 기본 엔티티, 전역 예외).
- `batch`: 예약된 작업 (재시도 로직 등).

### 2.2 헥사고날 아키텍처 (Ports & Adapters)
- **도메인 (Domain)**: 순수 Java/Kotlin. 프레임워크 의존성 없음 (Lombok 제외).
- **포트 (Ports / 인터페이스)**:
  - `port.in`: 유스케이스 / 서비스 인터페이스.
  - `port.out`: 리포지토리 / 외부 시스템 인터페이스.
- **어댑터 (Adapters / 구현체)**:
  - `adapter.in.web`: 컨트롤러 (`api` 모듈 내).
  - `adapter.out.persistence`: JPA 리포지토리 (도메인 모듈 내).

### 2.3 이벤트 기반 (Event-Driven)
- 모듈 간 통신에는 Spring `ApplicationEventPublisher`를 사용합니다.
- 가능하면 도메인 모듈 간의 직접적인 의존성을 **만들지 마십시오**.
- 예시: `OrderPlacedEvent` 발행 -> `Inventory`가 수신(listen)하여 재고 차감.

## 3. 코드 스타일 및 컨벤션 (Code Style & Conventions)

### 3.1 Java 스타일
- **들여쓰기**: 4칸 공백 (Space).
- **Import**: 표준 IntelliJ 순서. 꼭 필요한 경우가 아니면 와일드카드 import (`.*`)를 피하십시오.
- **Lombok**: 광범위하게 사용 (`@RequiredArgsConstructor`, `@Slf4j`, `@Getter`, `@Builder`).
- **DTO**: Java **Records** 사용 (`public record LoginRequest(...) {}`).
- **Null 가능성**: 명시적 체크 또는 `Optional` 사용.

### 3.2 명명 규칙 (Naming)
- **클래스**: PascalCase (`MemberService`).
- **메서드/변수**: camelCase (`registerMember`).
- **상수**: UPPER_SNAKE_CASE.
- **테스트**: 서술적인 이름 (예: `should_return_token_when_login_succeeds`).

### 3.3 컨트롤러 패턴
- `@RestController`, `@RequestMapping("/api/v1/...")` 사용.
- API 문서를 위해 `@Operation` (Swagger) 사용.
- `ResponseEntity<DTO>` 반환.
- **절대 엔티티를 직접 반환하지 마십시오**. 항상 DTO로 매핑하십시오.

### 3.4 에러 처리 (Error Handling)
- 로직 오류에는 `BusinessException` 사용.
- `common` 또는 도메인 모듈에 정의된 커스텀 예외를 throw 하십시오.
- 전역 처리는 `api` 모듈(`GlobalExceptionHandler`)에 있습니다.

## 4. 테스트 (Testing)

- **프레임워크**: JUnit 5, Mockito.
- **위치**: `src/test/java`.
- **스타일**: BDD 스타일 (`given`, `when`, `then`).
- **커버리지**: 도메인 서비스 및 중요 경로(critical paths)의 통합에 집중하십시오.

## 5. 에이전트 행동 프로토콜 (Agent Behavior Protocols)

1.  **검증 (Verification)**: 모든 코드 변경 후, 단일 테스트 명령어를 사용하여 관련 테스트를 실행하십시오.
    - `bash ./gradlew :<module>:test --tests "..."`
2.  **모듈 존중 (Modular Respect)**: 모듈 간 순환 의존성을 만들지 마십시오.
    - 모듈 A가 모듈 B를 필요로 할 경우, `settings.gradle` 및 `build.gradle`을 확인하십시오.
    - 부수 효과(side effects)에는 이벤트를 선호하십시오.
3.  **무작위 수정 금지 (No Blind Fixes)**: 테스트가 실패하면 스택 트레이스를 분석하십시오. 코드를 무작위로 변경하지 마십시오.
4.  **Java 25 기능**: 최신 기능(Records, Pattern Matching, Switch Expressions)을 자유롭게 사용하십시오.
