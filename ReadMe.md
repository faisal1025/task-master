# TaskMaster — A Collaborative Task Tracking System

This README explains the project, how to run it locally, the main concepts used (JWT stateless authentication, Spring Security, Spring Data JPA, auditing, pagination, authorization, filters/interceptors), the API endpoints and their behavior, and debugging tips.

---

## Table of contents

- Project overview
- Technical concepts used
- Prerequisites
- Local setup and run
- Configuration (important application.yml notes)
- API endpoints (summary + details)
- Security model (JWT, stateless, roles)
- Creator-only protection (how it's enforced)
- Auditing (CreatedBy / LastModifiedBy) and AuditorAware details
- Debugging & common issues (duplicate mapping, Auditor recursion, transactions)
- Development helpers

---

## Project overview

TaskMaster is a Spring Boot application for collaborative task tracking. Users can create tasks, assign tasks to other users, update and delete tasks. The app uses JWT-based stateless authentication, roles for authorization, and JPA for persistence.

Primary entities:
- `User` — application user (email, password, role, created tasks, assigned tasks)
- `Task` — task with title/description/dueDate/status, `creator` and `userAssigned` relations

---

## Technical concepts used

- Spring Boot (application skeleton)
- spring-boot-starter-security for authentication and authorization
- JWT (JSON Web Token) for stateless authentication
- Spring Data JPA (repositories, paging) with Hibernate
- JPA auditing (`@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy`) with an `AuditorAware` implementation
- Pagination via Spring Data `Pageable` and sorting
- Authorization via method-level `@PreAuthorize` and a custom `@CreatorOnly` annotation enforced by a `HandlerInterceptor`
- Interceptors vs Filters: we use an interceptor for annotation-based checks (method-level access) because it runs after handler resolution

---

## Prerequisites

- Java 17+ (project compiled with modern Spring Boot / Hibernate)
- Gradle (or use the included Gradle wrapper)
- MySQL running locally (or change datasource in `application.yml`)

---

## Local setup and run

1. Configure MySQL and create DB (or allow Spring Boot to create it):

   - Default DB URL in `src/main/resources/application.yml`:
     `jdbc:mysql://localhost:3306/taskMaster?createDatabaseIfNotExist=true`

2. Build the project (Windows example using the Gradle wrapper):

```bash
cd D:\AirtribeProjects\TaskMaster
gradlew.bat clean build -x test
```

3. Run the app locally:

```bash
gradlew.bat bootRun
```

Or run the produced jar:

```bash
java -jar build/libs/TaskMaster-1.0-SNAPSHOT.jar
```

4. The server listens on the port configured in `application.yml` (default `9090`).

---

## Configuration notes (`application.yml`)

- Do not hard-code a Hibernate dialect class that isn't on the classpath (e.g. `org.hibernate.dialect.MySQL8Dialect`) — this will cause ClassLoadingException. Prefer autodetection or use a dialect present in the Hibernate version you use.
- Example (already configured):
  - `spring.jpa.hibernate.ddl-auto: update` (for development only)
  - `spring.jpa.show-sql: true` (useful for debugging queries)
- JWT secret and expiration are under `jwt.*` keys in `application.yml`. Replace the secret in production.

---

## API endpoints

This is a concise list of the main endpoints implemented in the app (adjust paths if you changed `@RequestMapping`):

AuthController (authentication / registration)
- `POST /api/auth/register` — register a new user (body includes email, password, fullName)
- `POST /api/auth/login` — authenticate, returns JWT on success
- `GET /api/auth/verify?token=...` — verify email token (if email verification is implemented)

UserController
- `GET /api/users/me` — returns authenticated user's profile (requires authentication)
- `GET /api/users/{id}` — get user by id (admin or as implemented)

TaskController (main task endpoints)
- `POST /api/tasks/` — create a task (authenticated). `@CreatedBy` will set the task creator when AuditorAware is configured.
  - Body: { title, description, dueDate, assignUser (optional) }
  - Response: created Task with Location header
- `GET /api/tasks/` — list tasks with pagination and sorting (params: page, size, sort, dir)
- `GET /api/tasks/{id}` — get a single task
- `PUT /api/tasks/{id}` — update a task (requires authenticated user and creator-only protected by `@CreatorOnly`, or ADMIN)
  - Body: TaskUpdateRequest (fields optional)
- `DELETE /api/tasks/{id}` — delete a task (requires creator or ADMIN)

Dev endpoints (development helpers)
- `POST /dev/tasks/{id}/force-title?title=...` — (dev only) force update the title using a REQUIRES_NEW transaction (useful to verify commits)
- `GET /dev/tasks/{id}` — read a task quickly for testing

Behavior notes
- When creating tasks, the `creator` is set via JPA auditing (`@CreatedBy`) if AuditorAware returns the current user.
- When deleting a `User`, tasks created by that user can be removed via cascade if configured; tasks assigned to the user but created by other users will remain and the `userAssigned` FK is nulled (this project uses a `@PreRemove` hook in `User` to nullify assignments).

---

## Security model

- JWT stateless authentication: the server does not store session state. The client stores the JWT and sends it in `Authorization: Bearer <token>` header.
- `spring-boot-starter-security` config (see `SecurityConfig`) sets up the `JwtAuthenticationFilter` in front of protected endpoints.
- Roles: `ROLE_USER`, `ROLE_ADMIN` (example). Method-level checks use `@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")` where appropriate.
- Auditing: `AuditorAware<User>` implementation is used for `@CreatedBy` and `@LastModifiedBy`.

Important Auditor note (avoid recursive repository calls):
- The `AuditorAware` must not call JPA repositories directly during an entity flush, because the repository call can trigger auditing again and cause infinite recursion (StackOverflowError). Instead:
  - Prefer to return a `User` reference via `EntityManager.getReference(User.class, id)` if the authenticated principal exposes the user id, or
  - Return Optional.empty() when the auditor cannot be resolved safely.

---

## Creator-only protection (PUT/DELETE)

- This project introduces a custom annotation `@CreatorOnly` and a `TaskOwnerInterceptor` (Spring `HandlerInterceptor`) that enforces the rule: only the task `creator` (or users with ADMIN role) can update or delete the task.
- How it works:
  - `@CreatorOnly` is placed on controller methods (or on controller class).
  - `TaskOwnerInterceptor` is registered for `/api/tasks/**` in `WebConfig`.
  - On requests to annotated handlers, the interceptor extracts the `{id}` path variable, loads the `Task` via `TaskRepository`, resolves the current authenticated user (from principal or by email lookup), and compares ids. If mismatch, responds 403.
- Why interceptor vs filter: Interceptor runs after handler resolution, so it can inspect method-level annotations easily.

---

## Auditing (CreatedBy / LastModifiedBy)

- Implemented via Spring Data JPA auditing (`@EnableJpaAuditing` in the application) and an `AuditorAware<User>` bean.
- Important implementation detail: the auditor bean should not perform repository calls during a flush. Instead it should:
  - Extract user id from the authentication principal (preferred), then return `entityManager.getReference(User.class, id)` to provide a managed reference without loading; OR
  - If id is not available, return Optional.empty() and set `creator` manually in service code when necessary.

---

## Common issues & debugging

1. Duplicate table mapping 'user'
   - Caused by incorrect use of `@JoinTable(name = "user")` on a `@ManyToOne` relation (this creates a second mapping named `user`). Fix: use `@JoinColumn(name = "assigned_user_id")` for the `userAssigned` relation in `Task`.

2. Transactions not committing
   - Ensure `@Transactional` is the Spring annotation (`org.springframework.transaction.annotation.Transactional`).
   - Ensure the transactional bean is proxied (the service should be a Spring-managed bean, e.g. `@Service`), and the call is not a self-invocation.
   - Use logging to inspect `TransactionSynchronizationManager.isActualTransactionActive()` inside the method.
   - For debugging, a `@Transactional(propagation = Propagation.REQUIRES_NEW)` helper forces a separate transaction.

3. Authentication principal contents
   - If you use custom `UserDetails` that contains the user id, prefer to extract id from the principal in `AuditorAware` and in the `TaskOwnerInterceptor` to avoid extra DB lookups.

---

## Example requests

Authenticate:

```bash
curl -X POST http://localhost:9090/api/auth/login -H "Content-Type: application/json" -d '{"email":"user@example.com","password":"pass"}'
```

Create task (authenticated):

```bash
curl -X POST http://localhost:9090/api/tasks/ -H "Authorization: Bearer <token>" -H "Content-Type: application/json" -d '{"title":"New Task","description":"...","dueDate":"2026-03-10T12:00:00"}'
```

Update task (only creator or admin):

```bash
curl -X PUT http://localhost:9090/api/tasks/123 -H "Authorization: Bearer <token>" -H "Content-Type: application/json" -d '{"title":"Updated"}'
```

Force update in new transaction (dev):

```bash
curl -X POST "http://localhost:9090/dev/tasks/123/force-title?title=debugged"
```

---

## Notes & next steps

- Replace in-memory / local settings and JWT secret before deploying to production.
- Add tests for security and auditing.
- Consider optimizing AuditorAware to read user id from JWT claims so no DB lookup or reflection is needed.

---

If you want, I can:
- Update the `AuditorConfig` to use `entityManager.getReference(...)` and show the exact code used in this repo, or
- Add example Postman collection / curl scripts for all endpoints, or
- Create a small integration test that validates creator-only enforcement.


