# 0001 — PostgreSQL em produção e Flyway para o esquema

- **Status**: aceita
- **Data**: 2026-11-24

## Contexto

Até aqui o banco era um H2 em memória e as tabelas eram criadas pelo Hibernate (`ddl-auto`).
Os dados sumiam a cada reinício e não havia histórico de como o banco mudava.

## Decisão

- **PostgreSQL** no perfil `prod`; **H2 em memória** no perfil `dev` (rodar sem instalar nada).
- O esquema é criado **somente por migrations do Flyway** em `src/main/resources/db/migration`.
  O Hibernate só **valida** (`ddl-auto: validate`) se as entidades batem com as tabelas.
- As migrations usam SQL padrão, compatível com os dois bancos.
- Dados de demonstração ficam em `db/seed` e só são carregados no perfil `dev`.

## Consequências

- O banco passa a ter histórico versionado, igual ao código (V1, V2, ...).
- Regra: **nunca editar uma migration já aplicada**; toda mudança é uma migration nova.
- Divergências entre entidade e tabela aparecem na inicialização, e não em produção.
- Um teste com Testcontainers garante que as migrations rodam no PostgreSQL real (no CI).
