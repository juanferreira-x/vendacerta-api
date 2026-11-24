package com.api.vendacerta;

import static org.assertj.core.api.Assertions.assertThat;

import com.api.vendacerta.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Sobe a aplicação contra um PostgreSQL de verdade, num container Docker temporário (Testcontainers).
 * Prova que as migrations do Flyway e o mapeamento das entidades funcionam no banco de produção,
 * não só no H2. Sem Docker na máquina, o teste é pulado (no CI do GitHub ele sempre roda).
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class PostgresIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Migrations e dados de demonstração rodam no PostgreSQL e as entidades batem com as tabelas")
    void shouldMigrateAndValidateSchemaOnPostgres() {
        assertThat(productRepository.count()).isPositive();
    }
}
