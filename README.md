# VendaCerta API

[![CI](https://github.com/juanferreira-x/vendacerta-api/actions/workflows/ci.yml/badge.svg)](https://github.com/juanferreira-x/vendacerta-api/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

🇧🇷 [Leia em português](README.pt-BR.md)

**Open-source online store platform for small Brazilian businesses, with Mercado Pago, ready to run with
Docker.** Each installation is one store, customized by its owner, with no platform fees.

This repository is the REST API, built as a portfolio project.
Today it manages products and sales with transactional stock control; the roadmap adds a catalog,
authentication, cart and orders, Mercado Pago payments, a storefront and an admin area.

## Tech stack

Java 25 · Spring Boot 4.1 · Spring Web MVC · Spring Data JPA · Bean Validation · Flyway ·
PostgreSQL (prod) / H2 (dev) · springdoc OpenAPI · JUnit 5 · Mockito · Testcontainers · Docker

## Run locally (dev profile, no setup)

Requires JDK 25+.

```bash
./mvnw spring-boot:run
```

- API: <http://localhost:8080/api/v1/products> (comes with fictional demo products)
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Health: <http://localhost:8080/actuator/health>
- H2 console: <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:mem:vendacerta`, user `sa`, empty password)

The dev database lives in memory and is recreated on every restart.

## Run with Docker (prod profile + PostgreSQL)

Requires Docker.

```bash
cp .env.example .env      # then change POSTGRES_PASSWORD
docker compose up --build
```

The API starts on <http://localhost:8080> with an empty PostgreSQL database.
Swagger, the H2 console and demo data are disabled in the `prod` profile.

## Endpoints

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/products` | Create a product (`201` + `Location`) |
| `GET` | `/api/v1/products` | List products, paginated: `?page=0&size=20&sort=price,desc` (max size 50; sort by `id`, `name` or `price`) |
| `GET` | `/api/v1/products/{id}` | Get one product |
| `POST` | `/api/v1/sales` | Register a sale and decrease stock (all or nothing) |

Errors follow [RFC 7807](https://www.rfc-editor.org/rfc/rfc7807) (`application/problem+json`):
`400` invalid input (with an `errors` list), `404` not found, `409` concurrent update,
`422` business rule (e.g. insufficient stock), `500` generic error without internal details.

## Tests

```bash
./mvnw test     # unit, web slice and integration tests
./mvnw verify   # tests + coverage report (target/site/jacoco) + formatting check
./mvnw spotless:apply   # fix formatting
```

The PostgreSQL integration test uses Testcontainers and is skipped when Docker is not available.

## Configuration

| Variable | Profile | Description |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | all | `dev` (default) or `prod` |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | prod | PostgreSQL connection |

Secrets are never committed: see `.env.example`.

## Architecture decisions

See [`docs/adr`](docs/adr).

## License and disclaimer

[MIT](LICENSE). This project is for study and demonstration. Anyone using it for a real business must
review security, privacy law (LGPD) and tax obligations.
