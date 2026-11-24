-- Dados de demonstração da loja fictícia "Casa Aurora". Usado SOMENTE no perfil dev.
-- "R__" = migration repetível: roda de novo sempre que este arquivo mudar.
-- O "WHERE NOT EXISTS" evita duplicar produtos se ela rodar mais de uma vez.

INSERT INTO products (name, price, stock_quantity)
SELECT 'Caneca de Cerâmica Aurora', 39.90, 25
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Caneca de Cerâmica Aurora');

INSERT INTO products (name, price, stock_quantity)
SELECT 'Camiseta Algodão Aurora', 79.90, 40
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Camiseta Algodão Aurora');

INSERT INTO products (name, price, stock_quantity)
SELECT 'Caderno Pontilhado A5', 34.50, 60
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Caderno Pontilhado A5');

INSERT INTO products (name, price, stock_quantity)
SELECT 'Vela Aromática Lavanda', 49.00, 15
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Vela Aromática Lavanda');

INSERT INTO products (name, price, stock_quantity)
SELECT 'Ecobag Estampada', 29.90, 0
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Ecobag Estampada');
