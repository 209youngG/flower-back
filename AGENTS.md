# flower-back Agentic Coding Protocol

**Updated:** 2026-01-26
**Architecture:** Pragmatic Modular Monolith (Layered Internals)
**Language:** Java 25 (Preview Features Allowed)
**Framework:** Spring Boot 4.0.2 + Gradle 9.2.1

## 1. ARCHITECTURE STANDARD 🏗️

This project follows a **Modular Monolith** approach.
Code is organized by **Business Domains (Modules)**, not technical layers.

### 1.1 Module Structure
- **`api`**: The **Entry Point**. Contains Controllers (`adapter.in.web`), Swagger, and Global Config.
  - *Dependency Direction:* `api` -> `member`, `order`, `product`...
- **`common`**: Shared Kernel. Contains Base Entities, Global Exceptions, and Event definitions.
- **Domain Modules** (`member`, `order`, `product`, `inventory`, `delivery`):
  - Contains **Business Logic**, **Persistence**, and **Service Layer**.
  - **Internal Layering**:
    - `controller` (Avoid if possible, prefer `api` module for REST)
    - `service` (Business Logic)
    - `repository` (JPA/Hibernate)
    - `domain` (Entities)
    - `dto` (Data Transfer Objects)

### 1.2 Hexagonal -> Pragmatic Migration
*Note: The project aims for Hexagonal but currently implements a Layered style.*
- **New Features**: Prefer using `port.in` (UseCase) and `port.out` (Port) interfaces if creating complex logic.
- **Existing Features**: Follow the existing `Service` -> `Repository` pattern to maintain consistency.

### 1.3 Event-Driven Communication 📡
- **Strict Rule**: Domain modules should NOT depend on each other directly for side effects.
- **Pattern**:
  1. `OrderService` publishes `OrderPlacedEvent`.
  2. `InventoryEventListener` (in `inventory` module) consumes it.
  3. `InventoryService` updates stock.
- **Tool**: Use Spring `ApplicationEventPublisher`.

## 2. CODING CONVENTIONS 📝

### 2.1 Java 25 & Spring Boot
- Use **Records** for all DTOs (`public record MemberResponse(...) {}`).
- Use **Lombok** (`@RequiredArgsConstructor`, `@Builder`, `@Slf4j`) aggressively.
- Use `var` for local variables where types are obvious.
- **Null Safety**: Use `Optional<T>` for return types that might be empty.

### 2.2 Controller Rules (in `api` module)
- Return `ResponseEntity<ApiResponse<T>>`.
- **NEVER return Entities**. Always map to DTOs.
- Use `@Operation` (Swagger) for documentation.

### 2.3 Testing Strategy 🧪
- **Unit Tests**: JUnit 5 + Mockito. Focus on `Service` logic.
- **Integration Tests**: `@SpringBootTest` (limited usage) or `@DataJpaTest`.
- **Naming**: `should_expectedBehavior_when_state()` (snake_case for tests is allowed/encouraged).
- **Command**: `./gradlew :<module>:test` (e.g., `./gradlew :member:test`).

## 3. WORKFLOW PROTOCOL 🚀

1.  **Analysis**:
    - Before changing code, understand the **Module Dependencies** (`build.gradle`).
    - Check for existing **Events** that might be triggered.

2.  **Implementation**:
    - Create/Modify Domain Logic (`Service`/`Entity`).
    - Create/Update DTOs (Records).
    - Implement Controller in `api` module.
    - **Add Tests**.

3.  **Verification**:
    - Run **ONLY** relevant tests: `./gradlew :module:test --tests "Package.Class"`.
    - Do NOT run `./gradlew test` (all tests) unless necessary (it's slow).

4.  **Commit**:
    - Message Language: **Korean (한국어)**.
    - Format: `type: Subject` (e.g., `feat: 회원가입 API 구현`, `fix: 재고 차감 로직 수정`).
