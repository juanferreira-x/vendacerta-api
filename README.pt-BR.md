# VendaCerta API

[![CI](https://github.com/juanferreira-x/vendacerta-api/actions/workflows/ci.yml/badge.svg)](https://github.com/juanferreira-x/vendacerta-api/actions/workflows/ci.yml)
[![Licença: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

🇺🇸 [Read in English](README.md)

API REST open source para um **e-commerce de loja única**, feita como projeto de portfólio.
Hoje ela gerencia produtos e vendas com controle transacional de estoque; o roadmap adiciona catálogo,
autenticação, carrinho e pedidos, pagamentos com Mercado Pago, vitrine e área administrativa.

## Tecnologias

Java 25 · Spring Boot 4.1 · Spring Web MVC · Spring Data JPA · Bean Validation · Flyway ·
PostgreSQL (prod) / H2 (dev) · springdoc OpenAPI · JUnit 5 · Mockito · Testcontainers · Docker

## Rodar localmente (perfil dev, sem configuração)

Precisa do JDK 25 ou superior.

```bash
./mvnw spring-boot:run
```

- API: <http://localhost:8080/api/v1/products> (já vem com produtos fictícios de demonstração)
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Health: <http://localhost:8080/actuator/health>
- Console do H2: <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:mem:vendacerta`, usuário `sa`, senha vazia)

O banco de dev fica em memória e é recriado a cada reinício.

## Rodar com Docker (perfil prod + PostgreSQL)

Precisa do Docker.

```bash
cp .env.example .env      # depois troque o POSTGRES_PASSWORD
docker compose up --build
```

A API sobe em <http://localhost:8080> com um PostgreSQL vazio.
Swagger, console do H2 e dados de demonstração ficam desligados no perfil `prod`.

## Endpoints

| Método | Caminho | Descrição |
|---|---|---|
| `POST` | `/api/v1/products` | Cria um produto (`201` + `Location`) |
| `GET` | `/api/v1/products` | Lista os produtos |
| `GET` | `/api/v1/products/{id}` | Busca um produto |
| `POST` | `/api/v1/sales` | Registra uma venda e abate o estoque (tudo ou nada) |

Os erros seguem a [RFC 7807](https://www.rfc-editor.org/rfc/rfc7807) (`application/problem+json`):
`400` entrada inválida (com a lista `errors`), `404` não encontrado, `409` atualização simultânea,
`422` regra de negócio (ex: estoque insuficiente), `500` erro genérico sem detalhes internos.

## Testes

```bash
./mvnw test     # testes unitários, da camada web e de integração
./mvnw verify   # testes + relatório de cobertura (target/site/jacoco) + checagem de formatação
./mvnw spotless:apply   # corrige a formatação
```

O teste de integração com PostgreSQL usa Testcontainers e é pulado quando o Docker não está disponível.

## Configuração

| Variável | Perfil | Descrição |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | todos | `dev` (padrão) ou `prod` |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | prod | Conexão com o PostgreSQL |

Segredos nunca vão para o repositório: veja o `.env.example`.

## Decisões de arquitetura

Veja [`docs/adr`](docs/adr).

## Licença e aviso

[MIT](LICENSE). Este projeto é para estudo e demonstração. Quem usar em um negócio real deve revisar
segurança, a LGPD e as obrigações fiscais.
