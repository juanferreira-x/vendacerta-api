# 0002 — Lock otimista para proteger o estoque

- **Status**: aceita
- **Data**: 2026-11-24

## Contexto

Duas vendas simultâneas do mesmo produto podiam ler o mesmo estoque (ex: 5), abater cada uma
a sua quantidade e salvar por cima uma da outra ("lost update"). Resultado: vender mais do que existe.

## Decisão

Usar **lock otimista** com o campo `@Version` em `Product` (coluna `version`). A cada atualização o
Hibernate confere se a versão ainda é a mesma que foi lida; se outra transação mudou antes, a
atualização falha com `OptimisticLockingFailureException`, a transação inteira é desfeita e a API
responde **409 Conflict** pedindo para tentar novamente.

Alternativa descartada por enquanto: **lock pessimista** (`SELECT ... FOR UPDATE`), que trava a linha
até o fim da transação. É mais seguro em alta disputa, porém mais lento e sujeito a deadlocks.
Para uma loja pequena, conflitos são raros e o lock otimista é mais simples.

## Consequências

- Nenhuma venda sobrescreve o estoque calculado por outra.
- Em caso raro de conflito, o cliente recebe 409 e repete a operação.
- Se a disputa crescer (ex: promoções), reavaliar lock pessimista ou retentativa automática.
