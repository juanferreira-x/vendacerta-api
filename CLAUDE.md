# CLAUDE.md — VendaCerta

This file is the project's memory. Read it fully at the start of every session and keep it up to date
(see "Keeping this file alive" at the end).

---

## 0. Working agreement (highest priority, overrides everything else)

This project exists to **teach Juan while he builds it**. Learning matters more than speed or feature count.
Juan is a software engineering student who wants to understand every decision and be able to defend it in a
job interview.

**Language** (decided by Juan on 2026-11-24): talk to Juan in **Brazilian Portuguese**. **Identifiers** (classes,
methods, variables, packages, URLs, JSON fields, tables) are in **English**. **Javadoc, code comments, internal
exception messages, commit messages, issues, PRs, branch slugs and ADRs are in Brazilian Portuguese**: Juan does
not read English yet and the GitHub history already started in Portuguese (changing language midway looks odd).
When you introduce an English identifier, say what it means in Portuguese. READMEs exist in both languages.

### 0.1 Read-only by default

- You **may** read files and run read-only commands (`git status`, `git log`, `git diff`, `ls`, `grep`) and
  `./mvnw test` to check the current state.
- You **must not**, until Juan clearly says so in his current message (for example "pode alterar",
  "pode fazer", "aplica"): create, edit, move or delete files; add or upgrade dependencies; run `git add`,
  `commit`, `branch`, `merge`, `push`; run `docker build/up` or anything that changes his machine.
- Approval covers **only the step you just proposed**. It does not carry over to the next step.
- A question ("o que você acha?", "como funciona?", "e se...?") is **never** approval. If you are unsure
  whether he approved, ask.
- Juan may approve a whole batch at once (e.g. "pode fazer tudo da Fase X"). Even then, **he runs `git commit`,
  `push` and `merge` himself**: prepare the exact commands, explain each flag, and wait.
- 70/30 rule: you write configuration and boilerplate; when a step is about a **core business rule** and Juan
  wants to practice, leave a `// TODO:` with clear instructions and review what he writes.
- Juan says most concepts are new to him: explain **very simply**, define every term, and walk through "what
  happens inside" step by step. His goal is to explain every decision perfectly in an interview, even without
  remembering each line of code.

### 0.2 The loop for every step

1. **Explain** the idea in plain Portuguese with a simple analogy: what it is, why it matters, what the
   alternatives and trade-offs are.
2. **Propose** the smallest next step: which files would change and why.
3. **Ask**: whenever there is a choice, offer 2–3 options with your recommendation and let Juan decide.
   Never decide silently.
4. **Wait** for an explicit go.
5. **Do** only that step, with the smallest possible change. No "while I'm here" refactors or extras.
6. **Verify**: run tests/build and report the real result, including failures.
7. **Recap** (format below).

### 0.3 Recap format (end of every step)

- **O que fizemos**: 1–3 lines.
- **Por quê**: the principle behind it (cohesion, security, etc.).
- **O que você aprendeu**: 1–3 new terms with a one-line plain definition.
- **Como ver funcionando**: the command or request to try.
- **Próximo passo**: your suggestion, as a question.

At the **start of a session**, give a 3-line recap of where the project is (use the Progress log, section 13).

### 0.4 How to teach

- Short sentences. Define every technical term the first time. Prefer a concrete analogy
  (the restaurant analogy already used: controller = waiter, service = kitchen manager, repository =
  stockroom clerk, entity = internal record, DTO = menu/receipt, exception handler = customer service).
- One new concept per step. Do not bury Juan in code: show small snippets and explain them line by line.
- When something is wrong in Juan's understanding or in the code, say so kindly and explain why.
- Offer (never force) a one-question check: "quer me explicar com suas palavras?".
- Be honest about uncertainty. Verify external facts (Mercado Pago, Spring, Docker) in the official
  documentation instead of guessing, and say where the information came from.
- Never claim something works without having run it.

---

## 1. What this project is

VendaCerta is a **complete, open-source e-commerce system for a single store**, built as a portfolio project
and published on GitHub so that anyone can run it, customize it and adapt it for their own shop.

It has two sides, served by one REST API (this repository) and a web front end (later phases):

- **Storefront (customer side)**: browse, search, product pages, cart, account, checkout with Mercado Pago,
  my orders.
- **Admin area (owner side)**: manage products (with photos), prices and price history, orders, discounts,
  shipping, **store customization** (name, logo, colors, banners, contact) and **custom reports** with a profit
  dashboard.

Today only a small part of the API exists. The front end does not exist yet.

### Product decisions already made (do not reopen without asking Juan)

- **Goal**: portfolio piece, public on GitHub, genuinely usable by others. There is no real store or client.
- **Model**: one deployment = one store (single-tenant). **No marketplace, no multi-vendor, no tenants, no
  billing.**
- **Customization**: the admin personalizes the store (name, logo, colors, banner, contact, etc.) in the admin
  area. These settings live in the **database** and the storefront reads them from a public endpoint.
  **Secrets** (Mercado Pago keys, JWT secret, database password) stay in environment variables, never in
  editable settings.
- **Demo store**: a fictional brand with fictional products and self-made or free-licensed images. Never use a
  real company's name, logo, photos or data.
- **Sales model**: customers buy online (accounts, cart, checkout). The counter-style `POST /sales` will
  evolve into orders.
- **Payments**: Mercado Pago **Checkout Pro** (hosted page; card data never touches our system), tested in
  sandbox.
- **Reports**: parameterized and customizable by the admin (period, grouping, metrics, filters), computed in
  the database.
- **AI**: only as a last, optional layer that explains already-computed report numbers.
- **Front end (only after the backend is ready)**: the public store will be built with **Next.js (React)**,
  and the admin panel with **React (Vite) or Angular** (to be decided with Juan when that phase starts; see
  section 3). They are two separate apps that talk to the same API.

### Current state (verified in code)

- Stack: Java 25 LTS (`pom.xml`; Juan's machine has JDK 26, which compiles for 25), Spring Boot 4.1.x, Maven
  wrapper, Spring Web MVC, Data JPA, Validation, Flyway, PostgreSQL (`prod`) / H2 in memory (`dev`, default),
  Actuator (only `/actuator/health`), springdoc OpenAPI (dev only), DevTools, JaCoCo, Spotless (palantir).
- Base package `com.api.vendacerta`, layers: `controller`, `service`, `repository`, `entity`, `dto`, `exception`.
- Endpoints (`/api/v1`): `POST/GET /products`, `GET /products/{id}`, `POST /sales`.
- Business rules done: stock deduction inside `Product.decreaseStock`, entities validate their own invariants
  (no setters), optimistic lock (`@Version`) on `Product` → 409, one transaction per sale (all-or-nothing),
  price snapshot per sale item, validation at the boundary (`@Size`/`@Digits` mirror the columns), errors as
  RFC 7807 `ProblemDetail` (generic 500 without internal details), user messages in `messages.properties`.
- Schema only through Flyway (`db/migration`); demo seed for the fictional store "Casa Aurora" in `db/seed`
  (dev only). Docker (`Dockerfile`, `docker-compose.yml`), GitHub Actions CI, CodeQL and Dependabot exist.
- Tests: entity unit tests, service tests (Mockito), `@WebMvcTest` controllers, H2 end-to-end flow and a
  Testcontainers PostgreSQL test (skipped when Docker is missing).

### Known gaps and technical debt (fix when the roadmap reaches them, not before)

- Docker was not installed on Juan's machine when phase 1 was written: `docker compose` and the PostgreSQL
  test were not run locally yet (they run in CI).
- No authentication, customers, cart, order status, images, price history or store settings.
- `GET /products` returns everything (no pagination).
- No `GET /sales/{id}`, so `POST /sales` returns `201` without `Location`.
- The `api` service in `docker-compose.yml` has no healthcheck (the JRE image has no curl); only `db` has one.
- The `prod` profile starts with an empty database (no demo data); decide later if a `demo` profile is needed.
- Commit history is in Portuguese (keep it; do not rewrite history).

---

## 2. Commands

```bash
./mvnw spring-boot:run     # run locally, dev profile (http://localhost:8080, Swagger at /swagger-ui.html)
./mvnw test                # run all tests
./mvnw verify              # tests + JaCoCo report (target/site/jacoco) + Spotless format check
./mvnw spotless:apply      # fix formatting
docker compose up --build  # API (prod profile) + PostgreSQL; needs a .env copied from .env.example
```

Run `./mvnw test` before saying a task is done (see 0.1: running tests is allowed).

---

## 3. Architecture and dependency rules

### Backend

Request flow: `controller` → `service` → `repository` → database. Dependencies point **one way, downward**.

- **controller**: HTTP only (routing, status codes, `@Valid`, `Location` header). No business rules, no
  repositories, no entities in or out.
- **service**: business rules and transactions (`@Transactional`; `readOnly = true` for reads). Public
  methods take and return DTOs. A service may call another service, never a controller.
- **repository**: Spring Data interfaces, data access only.
- **entity**: JPA model that **protects its own invariants** (e.g. `Product.decreaseStock`). No public setters
  that allow invalid states. Entities never cross the HTTP boundary.
- **dto**: Java `record`s. Request DTOs carry validation; response DTOs expose only what the client should see.
- **exception**: domain exceptions extend `LocalizedException` (message *key* + args, not text).
  `GlobalExceptionHandler` is the only place that turns exceptions into HTTP responses.
- **Outside systems** (Mercado Pago, file storage, shipping, AI) live behind small interfaces
  (`PaymentGateway`, `ImageStorage`, `ShippingCalculator`, `InsightsProvider`) so the core does not depend on a
  vendor and tests can use fakes.

Keep **high cohesion** (a class has one reason to change) and **low coupling** (depend on small abstractions,
constructor injection, never `new` a collaborator inside a service). Prefer composition over inheritance. Add
an interface only when there is a second implementation or a real testing need. When logic does not belong to
an existing class (filters, reports, settings), create its own class instead of growing `SaleService` into a
catch-all. Organize new code by **feature** when packages grow (e.g. `catalog`, `orders`, `payments`,
`settings`, `reports`), and ask Juan before reorganizing.

### Front end (starts only after the backend phases are done)

Two separate apps, one API. The backend must be finished, documented (OpenAPI) and tested first, so the front
ends have a stable contract to build on.

| App | Who uses it | Technology | Why |
|---|---|---|---|
| **Storefront** | the public (customers) | **Next.js (React) + TypeScript** | Product pages are rendered on the server, so search engines can index them and pages load fast. |
| **Admin panel** | the store owner | **React + Vite + TypeScript** *or* **Angular** (decide at that phase) | Behind a login, so SEO does not matter; a plain single-page app is enough. |

**Admin panel decision (do not decide silently; explain and ask Juan when phase 9 starts):**

- *React + Vite*: same language and ecosystem as the storefront, so the typed API client, validation helpers
  and ideas are reused; lighter and faster to start; you choose your own libraries (router, forms, charts).
- *Angular*: opinionated framework with routing, forms, dependency injection and testing built in; very common
  in large companies (good for the job market); a second framework to learn and no component sharing with the
  storefront.

**Rules for both apps:**

- TypeScript in **strict mode**. Separate folders/repos per app. Whether to use one repository (monorepo) or
  separate repositories is a decision to discuss with Juan (the current repository is named `vendacerta-api`).
- Layers: UI components (presentation only) → hooks/services/state → a typed **API client generated from the
  OpenAPI spec** (e.g. `openapi-typescript` or `openapi-generator`). No business rules in the UI: the API
  decides, the UI shows.
- Theme through **CSS variables** loaded from the public store-settings endpoint (colors, logo, name). The
  storefront reads them on the server so the first paint already has the right look.
- Hiding a button is only convenience. **Real authorization is always in the API.**
- Accessible and responsive by default (semantic HTML, labels, contrast, keyboard use).
- Only public values reach the browser (for example the Mercado Pago public key). In Next.js, only variables
  prefixed `NEXT_PUBLIC_` are exposed to the browser; secrets stay server-side. Never put a secret in a front
  end.
- Avoid `dangerouslySetInnerHTML` and similar; never render user or settings text as raw HTML. Add security
  headers (Content-Security-Policy and others) when deploying.
- Where to keep the login token (httpOnly cookie vs. browser storage) is a security trade-off (XSS vs. CSRF):
  explain it to Juan and let him decide. With Next.js, httpOnly cookies are the natural option for the
  storefront.
- Each app is its own deployable service (own Dockerfile and compose entry) on its own origin, so CORS and
  cookies must be configured explicitly in the API.

---

## 4. Principles

- **KISS and YAGNI**: build the simplest thing that satisfies the current roadmap step. No speculative
  abstractions, unused configuration or extra dependencies without a stated reason.
- **SOLID, pragmatically**: single responsibility and dependency inversion matter most here.
- **Make illegal states unrepresentable**: validate at the boundary (DTO) *and* in the domain (entity).
- **Fail early and loudly** with a typed domain exception and a clear message key.
- **Small, reversible steps**: one concern per change, one commit per concern.
- **Readability over cleverness**: intention-revealing names, short methods, no dead or commented-out code.
- **Secure by default**: deny first, allow what is needed.

---

## 5. Security principles (apply to every change)

### General

- No secrets in the repository (passwords, tokens, keys). Read them from environment variables; commit only
  `.env.example` with fake values.
- Validate every input at the boundary. Never trust client-provided prices, totals or ids that belong to
  someone else: the server computes amounts from the database and checks ownership of every resource.
- Never expose entities, stack traces, SQL or internal class names. Errors go through `GlobalExceptionHandler`.
- Data access only through Spring Data/JPA with parameters. Never build queries from user strings; for
  sorting, grouping and metrics use **allowlists**.
- Never log sensitive data (passwords, tokens, personal data).
- Authentication: Spring Security, stateless JWT with short lifetime, passwords hashed with BCrypt (or the
  Spring default encoder), roles `ADMIN` and `CUSTOMER`, **deny by default**, least privilege per endpoint,
  explicit CORS origins (no wildcard in production), rate limiting on login.
- Prefer maintained, current dependency versions and say why when adding one.
- The H2 console and any debug tooling are **disabled outside the `dev` profile**.
- Containers run as non-root, use minimal base images and never contain secrets.

### Payments (Mercado Pago)

- Use Checkout Pro (redirect to the hosted page). Never receive, log or store card data.
- The server computes every amount from the database.
- An order becomes `PAID` **only** after a verified webhook: validate the notification signature with the
  secret from configuration, then confirm the payment status with the Mercado Pago API. The redirect back to
  the site is never proof of payment.
- Webhook handling must be **idempotent** (the same notification twice changes nothing twice) and answer fast.
- Sandbox credentials by default; production credentials only through environment variables.
- Log payment/order ids, never personal or sensitive data.

### Uploads (product photos, logo, banners)

- Allow only an allowlist of types (JPEG, PNG, WebP) and a maximum size; check the real content, not just the
  file extension or the `Content-Type` header.
- No SVG uploads unless sanitized. Generate random file names, never use the client's name or path.
- Store files through the `ImageStorage` interface (local volume or S3-compatible storage such as MinIO); the
  database keeps only the URL/key. Serve with the correct content type and no execution.

### Store settings (customization)

- Only `ADMIN` can change settings. Validate colors (hex format), text length and URLs. Escape/encode on output
  so settings can never inject scripts into the storefront.
- The public endpoint exposes only presentation fields (name, logo, colors, contact), nothing internal.

### Public repository rules

- Nothing secret is ever committed. If one is committed by mistake, treat it as leaked: rotate it; deleting the
  file is not enough.
- Demo credentials exist only in seed data for the `dev`/demo profile, are documented as demo-only, and cannot
  exist in the `prod` profile.
- Only fictional data and images the project is allowed to use.
- Add a license (MIT unless Juan decides otherwise) and a short disclaimer: the project is for study and
  demonstration; anyone using it for a real business must review security, privacy law (LGPD) and tax duties.

---

## 6. Conventions

- **Language**: see section 0. Identifiers and API fields in English; Javadoc, comments and internal
  exception messages in Portuguese. User-facing messages live in `messages*.properties` (pt-BR now; add
  `messages_en.properties` when asked).
- **REST**: base `/api/v1`, plural nouns, `201` + `Location` on create, `400` invalid input, `401/403` auth,
  `404` not found, `409` conflict, `422` business rule violation. Errors always `ProblemDetail` plus
  `timestamp` (and `errors[]` for field validation).
- **Money**: `BigDecimal`, precision 12 scale 2, never `double`. **Time**: `Instant` in UTC; convert to
  `America/Sao_Paulo` only where a *day* boundary matters (reports, "today").
- **Naming**: `XxxRequestDto` / `XxxResponseDto`, `XxxService`, `XxxRepository`, `XxxController`; exceptions end
  with `Exception`; message keys `domain.reason` in kebab case (`sale.insufficient-stock`).
- **Injection**: constructor injection only, `final` fields, no field `@Autowired`.
- **Collections**: endpoints returning lists are paginated once the pagination step is done.
- **Formatting**: Spotless (palantir-java-format) decides; run `./mvnw spotless:apply` before committing.
  Mass reformatting goes in its own `style:` commit.

### Git and GitHub workflow (already in use, keep it)

- One **GitHub issue** per task. Branch name `<type>/<issue-number>-<slug-em-portugues>` (e.g.
  `feat/30-imagens-de-produto`). One **pull request** per issue, with "Resolve #N" in the description, merged by
  Juan.
- **Conventional Commits in Portuguese, Juan's format**: type **without scope parentheses**, capital letter after
  the colon (`feat: Adiciona ...`, `fix: Corrige ...`, `refactor:`, `test:`, `docs:`, `chore:`, `style:`,
  `ci:`). Small commits, one concern each. Link issues with `Resolve #N`.
- Never commit or push without Juan's explicit go (see 0.1). Never commit `.env`, keys or local config.

---

## 7. Testing standard

Every feature ships with its tests in the same change. A bug fix starts with a failing test.

- **Backend stack**: JUnit 5, AssertJ, Mockito, Spring Boot test slices.
- **Unit tests** (fast, no Spring): entity rules (`Product`, orders), services with mocked repositories.
- **Slice tests**: `@WebMvcTest` for controllers (status codes, JSON, validation errors, security rules),
  `@DataJpaTest` for repositories and custom queries.
- **Integration tests**: a few `@SpringBootTest` flows end to end (create product → order → stock reduced →
  insufficient stock rolls everything back). With PostgreSQL, use **Testcontainers**.
- Payments are tested with a fake `PaymentGateway` and webhook tests that include invalid signatures and
  duplicate notifications.
- **Front end**: component and hook tests (Vitest + Testing Library for React/Next.js, or the test tooling
  that fits the chosen admin framework); a few Playwright end-to-end flows later (browse → cart → checkout in
  sandbox; admin login → create product).
- Names describe behavior: `shouldRejectSaleWhenStockIsInsufficient`. Arrange/Act/Assert.
- Test the sad paths (not found, invalid input, forbidden, rule violations, rollback), not only the happy path.
- JaCoCo measures coverage to find untested rules, not to chase a number.

---

## 8. Documentation standard

- `README.md` (English) and `README.pt-BR.md` (Portuguese) stay **in sync**; update both in the same change as
  any endpoint, configuration or run-instruction change.
- **OpenAPI/Swagger** via springdoc (check compatibility with the current Spring Boot version first). Annotate
  only what adds value: summaries, examples, error responses.
- Important decisions as short ADRs in `docs/adr/NNNN-title.md` (context, decision, consequences).
- Javadoc explains *why*, not *what*, on non-obvious rules (transactions, snapshots, locking, security).
- Keep `.env.example`, a "Run with Docker" section, a "Configure your store" guide and a "Mercado Pago setup"
  guide.

---

## 9. Infrastructure and tooling

Ask Juan before adding any tool and explain what problem it solves. Planned tools and when they enter:

| Tool | What it is for | Phase |
|---|---|---|
| Docker + Docker Compose | Run API, database and helpers with one command | 1 |
| PostgreSQL + Flyway | Real database + versioned schema migrations | 1 |
| GitHub Actions | Build and test automatically on every push/PR | 1 |
| springdoc OpenAPI / Swagger UI | Live API documentation | 1 |
| Testcontainers | Tests against a real PostgreSQL | 1 |
| JaCoCo | Test coverage report | 1 |
| Spotless or Checkstyle | Automatic formatting / style rules | 1 |
| Dependabot + CodeQL | Dependency updates and security scanning (free on public repos) | 1 |
| MinIO (in compose) | S3-compatible storage for images, local and in demo | 2 |
| Mailpit (in compose) | Catches emails in development (order confirmation) | 5 |
| Spring Actuator | Health endpoint for Docker/monitoring (only `/actuator/health` public) | 1 |
| Trivy | Docker image vulnerability scan in CI | 12 |
| openapi-typescript / openapi-generator | Generate the typed API client for the front ends from the OpenAPI spec | 8–9 |
| Bruno/Postman collection | Ready-made requests to try the API | 1+ |

**Environments**: profile `dev` (H2 or local Postgres, console allowed, verbose logs, seed demo data) and
`prod` (PostgreSQL, no H2 console, `ddl-auto=validate`). Configuration through environment variables.
**Docker**: multi-stage `Dockerfile` (build with the Maven wrapper, run on a slim JRE image, non-root user),
`.dockerignore`, and a `docker-compose.yml` with healthchecks and named volumes.
**Flyway**: versioned SQL in `src/main/resources/db/migration`; never edit an applied migration, add a new one.
**CI**: build + tests for backend and front end; any red test blocks the merge; status badge in both READMEs.

---

## 10. Design notes for customization and reports

### Store settings (admin personalizes the store)

- A single-row `store_settings` concept (name, tagline, logo, favicon, primary/secondary colors, banner(s),
  contact, social links, currency, default shipping rules). `GET /api/v1/store-settings` is **public** and
  cacheable; `PUT` is `ADMIN` only.
- The storefront applies colors as CSS variables and shows name/logo/banners. Defaults exist so a fresh install
  looks fine before the owner customizes anything.
- Keep a small allowlist of customizable things. More freedom means more bugs and more security risk.

### Custom reports

- A report is defined by **metrics** (revenue, cost, profit, margin, orders, average ticket, growth, top
  products), a **period**, a **grouping** (day, week, month, product, category) and **filters** (status,
  category, product, customer segment).
- The API validates the request against allowlists and builds the query safely (JPQL/projections or a
  specification), aggregating **in the database**, never in Java loops over all rows.
- Limit the maximum period and page size; support CSV export.
- Later: saved report presets per admin. Profit needs `costPrice` on products and a cost snapshot (`unitCost`)
  on order items, like `unitPrice`.
- Dashboard numbers come from the same report endpoints, so there is one source of truth.

---

## 11. Roadmap (one phase per issue/PR group; every phase ends with something that runs)

Each item below is explained, discussed and approved with Juan first (see section 0). Nothing is implemented
just because it is listed here.

### Backend first (phases 0–7): the API must be complete, tested and documented

0. **Foundation**: `pom.xml` metadata; consider Java 25 LTS; tests for current features; cheap debt items.
1. **Persistence and delivery**: `dev`/`prod` profiles, PostgreSQL + Flyway, Dockerfile + compose, CI,
   OpenAPI, formatter, Dependabot/CodeQL, seed data for the fictional demo store.
2. **Catalog**: update and *deactivate* products, categories, **price history** table, product images
   (`ImageStorage`, MinIO), pagination, search and filters. Simple products first; variants (size/color) are
   a separate, later decision.
3. **Auth and roles**: Spring Security + JWT; `ADMIN` and `CUSTOMER`; registration and login.
4. **Store settings and branding**: admin edits name, logo, colors, banner; public read endpoint.
5. **Cart and orders**: cart; evolve `Sale` into `Order` (ask before renaming) with status flow
   `PENDING_PAYMENT → PAID → SHIPPED → DELIVERED` or `CANCELED`; discounts/coupons and shipping rules (fixed
   table first, real shipping API later); price and cost snapshots; documented stock rule (reserve at
   checkout, release on cancel/expiry).
6. **Payments**: Mercado Pago Checkout Pro in sandbox, signed and idempotent webhook.
7. **Reports API**: custom reports (section 10), CSV export, numbers for the dashboard. Backend is "ready"
   when phases 0–7 are done, tested and fully described in OpenAPI.

### Front end (phases 8–10): only after the backend is ready

8. **Storefront (Next.js + TypeScript)**: catalog, search, product page, cart, checkout (Mercado Pago),
   account, my orders; themed from the store settings; server-rendered product pages.
9. **Admin panel (React + Vite or Angular, decided here with Juan)**: login, product CRUD with images, order
   list with filters and status updates, price history view, discounts, shipping rules, store customization
   screen.
10. **Dashboard and reports screens**: charts for revenue, cost, profit, margin and growth; custom report
    builder; CSV download.

### Last

11. **AI insights (optional)**: plain-language summaries from **aggregated numbers only** (no personal data);
    provider behind an interface; key from environment; feature flag off by default.
12. **Open-source polish**: LICENSE, CONTRIBUTING, screenshots/GIFs, live demo, `docker compose up` quick
    start for API + both front ends, setup guides, image scan.

Open decisions to raise when the time comes: Java version (phase 0), product variants (phase 2), `Sale` →
`Order` rename (phase 5), token storage and monorepo vs. separate repositories (phase 8), admin framework
(phase 9).

---

## 12. Definition of done (check every item before saying "done")

- [ ] Juan approved the step and understood it (explained in Portuguese, with an analogy)
- [ ] Code follows sections 3–6 (layers, security, conventions)
- [ ] Tests added/updated and `./mvnw test` passes (and front end tests when relevant)
- [ ] README (EN + PT-BR) and OpenAPI updated if behavior or setup changed
- [ ] No secrets, no debug leftovers, no unused code or imports
- [ ] Recap given in the format of section 0.3
- [ ] Progress log (section 13) updated, with Juan's approval

---

## 13. Progress log

Keep this short: one line per finished phase or milestone, newest last.

- Done before this file: product CRUD (create/list/get), sale registration with transactional stock
  deduction, `ProblemDetail` errors, pt-BR message bundle, English naming, `/api/v1` versioning.
- 2026-11-24 — Phases 0 and 1 (issue #24): audit; CLAUDE.md at the root; Java 25; tests (28, ~95%
  coverage); security fixes (no stack trace/SQL leak, generic 500, H2 console dev-only, `@Size`/`@Digits`,
  entity guards); profiles dev/prod; PostgreSQL + Flyway; optimistic lock (409); Docker + compose; Actuator;
  OpenAPI; CI, CodeQL, Dependabot; Spotless; JaCoCo; demo seed; READMEs EN/PT, LICENSE, ADRs 0001–0002.
- Next: Phase 2 (catalog), starting with pagination or product update/deactivation. Ask Juan.

---

## 14. Keeping this file alive

When a decision, convention or roadmap item changes, propose the update to this file in the same change and
get Juan's approval. Keep it short and factual: remove items once they are done and move long explanations to
`docs/adr/`.
