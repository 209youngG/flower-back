# Project Flower - Backend Renewal Plan

**Author:** Prometheus (Lead Planner)  
**Date:** 2026-01-21  
**Version:** 2.0.0 (Pragmatic Pivot)  
**Status:** **Approved** (Replaces v1.0.0)

---

## 1. 프로젝트 비전 (Project Vision)

**"복잡함은 거부한다. 실용성을 추구하되, 모듈의 경계는 사수한다."**

**Flower-Back**은 1인 개발 및 AI 협업 환경에 최적화된 **'실용적 모듈러 모놀리스(Pragmatic Modular Monolith)'**를 지향합니다.
오버엔지니어링(헥사고날 아키텍처의 무조건적 도입)을 지양하고, 빠른 개발 속도와 유지보수성의 균형을 맞춥니다.

### 1.1 핵심 가치 (Core Values)
1.  **Simplicity (단순함)**: 불필요한 추상화(Interface, Port)를 제거하여 코드 복잡도를 낮춥니다.
2.  **Modularity (모듈화)**: 모듈 간의 물리적 분리는 유지하되, 내부 구조는 유연하게 가져갑니다.
3.  **Velocity (속도)**: Boilerplate 코드를 최소화하여 AI와 개발자의 생산성을 극대화합니다.

---

## 2. 아키텍처 결정 사항 (Architecture Decision Record)

### 2.1 아키텍처 스타일 변경
- **AS-IS (v1.0.0 Plan)**: 헥사고날 아키텍처 (Ports & Adapters)
    - 모든 계층에 인터페이스 강제 (`Port.in`, `Port.out`).
    - DTO 매핑 비용 과다 발생.
- **TO-BE (v2.0.0 Plan)**: **실용적 계층형 아키텍처 (Pragmatic Layered Architecture)**
    - 모듈 **내부**는 직관적인 `Controller` -> `Service` -> `Repository` 구조 채택.
    - 단, 모듈 **간** 통신은 철저히 격리 (Public Service 또는 Event 사용).

### 2.2 패키지 구조 가이드라인
모듈 내부에서 `port`, `adapter` 패키지를 제거하고, 역할별 패키지로 단순화합니다.

**권장 구조 (`order` 모듈 예시):**
```text
com.flower.order
├── controller      # (Optional) 모듈 자체 컨트롤러가 필요할 경우
├── service         # 비즈니스 로직 (Public Class)
├── repository      # 데이터 접근 (Package-Private)
├── domain          # JPA Entity & Domain Logic
├── dto             # 데이터 전송 객체 (Record)
└── event           # 도메인 이벤트 리스너/발행
```

### 2.3 통신 원칙
1.  **모듈 내부**: `Service`가 `Repository`를 직접 호출.
2.  **모듈 외부 (`api` -> `order`)**: `api` 모듈의 컨트롤러는 `order` 모듈의 **Public Service**만 호출 가능.
    - ❌ `api`가 `order`의 `Repository`에 접근 불가 (Package-Private 권장).
    - ❌ `api`가 `order`의 `Entity`를 직접 반환 불가 (반드시 DTO 반환).
3.  **모듈 간 (`order` <-> `inventory`)**: **Spring Event**를 통한 비동기 통신 유지.

---

## 3. 도메인 로드맵 (Domain Roadmap)

파일럿 리팩토링 없이, 실용적 구조로 기능을 빠르게 완성하는 것에 집중합니다.

| 우선순위 | 도메인 | 상태 | 목표 및 작업 내용 |
|:---:|:---|:---:|:---|
| **P1** | **Order** | ✅ 완료 | `adapter`, `port` 패키지 제거. `Service` 중심 구조로 단순화. |
| **P1** | **Cart** | ✅ 완료 | 과도한 헥사고날 구조 제거. `CartService` 구현 단순화. |
| **P2** | **Payment** | ✅ 완료 | PG 연동 (Toss/Kakao) Mock 구현. 배송 정보 연동 보완. |
| **P2** | **Delivery** | ✅ 완료 | 배송 상태 추적 로직 및 결제 완료 이벤트 리스너 보완. |
| **P3** | **Member** | ✅ 유지 | 현재 구조 유지하되, OAuth2 등 인증 고도화. |

---

## 4. 상세 구현 지침 (Implementation Guidelines)

### 4.1 DTO 전략 (Java Records)
- **Request/Response**: `Java Record` 사용.
- **Mapping**: 별도 Mapper 클래스 없이 DTO 내부의 `static factory method` 또는 `constructor` 활용.
  ```java
  // Good Example
  public record OrderDto(Long id, String status) {
      public static OrderDto from(Order order) {
          return new OrderDto(order.getId(), order.getStatus().name());
      }
  }
  ```

### 4.2 영속성 (Persistence)
- **JPA**: 복잡한 쿼리는 `QueryDSL` 도입 고려 (현재는 JPQL/Method Name으로 충분).
- **Transaction**: `Service` 메서드 단위로 `@Transactional` 적용.

### 4.3 에러 처리
- `BusinessException`을 상속받은 구체적인 예외(`OrderNotFoundException` 등) 사용.
- `api` 모듈의 `GlobalExceptionHandler`에서 일괄 처리.

---

## 5. Next Actions for Sisyphus (Developer)

1.  **Cart 모듈 다이어트**: `adapter`, `port` 패키지를 삭제하고 `service`, `repository`로 플랫하게 만드십시오.
2.  **Order 모듈 단순화**: 복잡한 패키지 구조를 정리하고 기능 구현에 집중하십시오.
3.  **Payment 구현**: 실제 결제 승인 로직을 `Port` 인터페이스 뒤에 숨기는 전략(여기만 예외적으로 어댑터 패턴 사용)을 취하십시오.

---
**"Simple is Best. 코드는 짐이 아니라 자산이어야 한다."**
