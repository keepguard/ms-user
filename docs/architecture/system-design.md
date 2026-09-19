# ms-user - System Design

## 1. Propósito e Domínio
- **Responsabilidade Principal:** Microsserviço de gestão de usuários e perfis (PF/PJ): cadastro/registro com sessão em Redis, CRUD de endereço/contato/preferências de notificação, ciclo de vida de status e exclusão com anonimização (LGPD) + evento de erasure. Isola dados por `companyId`/`tenantId` no schema Postgres `ms_user`.
- **Domínio/Subdomínio:** IAM / User Management (identidade de titular, perfil, contatos e preferências).

## 2. Tech Stack Local
- **Linguagem & Framework:** Java 25 / Spring Boot 3.5.3 (`spring-boot-starter-parent`); Spring Web, Validation, AOP, Actuator; Spring Cloud BOM 2025.0.3; Lombok 1.18.40; Springdoc OpenAPI 2.3.0; Resilience4j 2.2.0 (circuit breaker, retry, bulkhead, rate limiter, time limiter, micrometer); Virtual Threads habilitados. Artifact: `com.keepguard:ms-user:1.0.28`. Libs internas: `lib-security` 1.0.4-SNAPSHOT (JWT), `lib-common` 1.0.37-SNAPSHOT (logging/métricas/audit), `lib-validation` 1.0.3-SNAPSHOT (moderação). `libphonenumber` 8.13.33.
- **Persistência e Cache:** PostgreSQL via Spring Data JPA/Hibernate (schema `ms_user`; `ddl-auto=update` em local/dev, `validate` em prod). Redis: standalone em local; cluster (nós 7000–7005) em dev/prod. Caches: `user_cache`, `notify_cache`, `register_session` (TTL user/notify 30d; register 20min).
- **Mensageria:** RabbitMQ (`spring-boot-starter-amqp`). Publica `user.erasure.requested` no exchange `keepguard-events-exchange` (routing key `user.erasure.requested`). Auditoria via exchange `srv-audit-exchange-{local|dev|prod}` (routing key `audit.event`, source `ms-user`). Não há consumers (`@RabbitListener`) neste serviço.

## 3. Arquitetura Interna
- **Padrão Utilizado:** Hexagonal (Ports & Adapters) + camadas DDD leves: `adapters/in` → `application` (ports/services/DTOs) → `domain` → `infrastructure` (adapters out: JPA, Redis, messaging). CQRS leve nos use cases (`*CommandService` / `*QueryService` + `*UseCaseService`). Strategies de perfil PF/PJ no domain e na application.
- **Módulos Principais:**
  - `adapters/in/rest` — Controllers: `user`, `register`, `address`, `contact`, `usernotify`, `internal`, `health` (+ DTOs/mappers).
  - `application/port/in` — `UserPort`, `RegisterPort`, `AddressPort`, `ContactPort`, `UserNotifyPort`.
  - `application/port/out` — persistence (`User`, `PersonProfile`, `CompanyProfile`, `Address`, `Contact`, `UserNotify`), cache (`User`, `Notify`, `Register`), `MetricsPort`.
  - `application/service` — use cases por agregado; strategies de profile/notification.
  - `domain` — entidades `User`, `PersonProfile`, `CompanyProfile`, `Address`, `Contact`, `Notify`, `RegisterSession`, `UserProfile`; enums de status/tipo/KYC/locale; validators (phone, locale); strategies PF/PJ.
  - `infrastructure` — JPA entities/adapters/mappers, Redis cache services, `UserErasureEventPublisher`, Security/Swagger/Resilience configs, `GlobalExceptionHandler`, filtros de correlation.

## 4. Superfície de Contato (I/O)
- **Endpoints Expostos Principais:**
  - **Users** (`/api/v1`): `POST /users`; `GET /users/{id|code/{codeUser}|email/{email}}`; `PUT /users/{id}`; `PATCH /users/{id}/person-document`; `DELETE /users/{id}`; `GET /companies/{companyId}/users` (paginado); lifecycle `PATCH .../activate|deactivate|block|unblock|suspend|unsuspend` + batch activate/deactivate. Header obrigatório `X-Company-Id`; `X-Tenant-Id` opcional na criação (fallback = companyId).
  - **Register** (`/api/v1/register`, públicos): `POST /init`, `/confirm`, `/resend`.
  - **Address / Contact** (`/api/v1`): CRUD + list/search por `userId`.
  - **Notify** (`/api/v1/users`): create/get/patch por `userId` ou `codeUser`.
  - **Internal** (`/internal/v1/users`): `GET /{id}`, `GET /code/{codeUser}` — exige `ROLE_SYSTEM` ou `ROLE_ADMIN` + `X-Company-Id`.
  - **Health** (`/api/v1/health`) + Actuator (`health`, `info`, `prometheus`). Portas: local `8585`; dev/prod `8085`.
- **Dependências Externas:**
  - **PostgreSQL** (`keepguard_api_db`, schema `ms_user`) e **Redis** (cache).
  - **RabbitMQ** — eventos de erasure e auditoria (`lib-common`).
  - **ms-auth** — emissor JWT (`issuer: ms-auth`, validação via `lib-security`).
  - **OpenAI Moderations API** (`api.openai.com/v1/moderations`, model `omni-moderation-latest`) via `lib-validation` (`@ModeratedContent` em DTOs de registro/perfil).
  - Referência lógica a **ms-company** (`companyId` em `CompanyProfile` / domínio PJ); sem client HTTP/Feign local — contrato por UUID e headers.
  - Consumidores implícitos do evento `user.erasure.requested` (serviços satélites da saga de exclusão).

## 5. Invariantes Locais e Observações
- **Multi-tenancy / isolamento:** `companyId` e `tenantId` obrigatórios no domínio `User`; operações REST escopadas por header `X-Company-Id`. Unicidade de e-mail considerada no escopo company/tenant.
- **Perfis 1:1:** `PersonProfile` e `CompanyProfile` com unique em `user_id`; em prod o Hibernate não cria o índice (`ddl-auto=validate`) — script SQL de dedup/unique deve rodar antes do deploy (`scripts/sql/2026-09-02-profile-user-id-unique.sql`).
- **Tipos e status:** `UserTypeEnum` PERSON/COMPANY; status `PENDING|ACTIVE|INACTIVE|BLOCKED|SUSPENDED|DELETED|ANONYMIZED`. Criação inicia em `PENDING`. Transições via métodos de domínio + endpoints públicos de lifecycle (incluindo compensação de SAGA no `DELETE`).
- **Exclusão LGPD:** `DELETE` anonimiza titular/perfil PF (não hard-delete imediato do agregado principal), invalida cache Redis e publica `user.erasure.requested` (falha de publish é apenas logada — best-effort).
- **Registro:** sessão em Redis (`register_session`, TTL 20min); máx. 5 tentativas de token e 5 de resend; senha com BCrypt (`PasswordEncoder`).
- **Endpoints públicos (`@PublicEndpoint`):** create user, person-document patch, delete, lifecycle de status, register init/confirm/resend, create notify, health — demais rotas exigem JWT (`@EnableJwtSecurity`).
- **Internal API:** gate explícito por role no controller (`ROLE_SYSTEM`/`ROLE_ADMIN`); destino típico BFF / chamadas inter-serviço.
- **Resiliência:** CircuitBreaker/Retry em Redis e operações de DB; Bulkhead em `databaseOperation`; health de CB/rate limiter no Actuator.
- **Observabilidade:** Micrometer/Prometheus, métricas por endpoint (`@MetricsEndpoint`), audit trail RabbitMQ, timezone default `America/Sao_Paulo`, multipart até 10MB.
- **Peculiaridade de tenant na criação:** header `X-Tenant-Id` opcional no controller; se ausente, mapper usa `companyId` como `tenantId`.
