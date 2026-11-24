package com.api.vendacerta;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Teste de integração de ponta a ponta: aplicação inteira + banco H2 com o esquema do Flyway.
 * Prova o fluxo real: criar produto → vender → estoque abatido → venda inválida desfaz tudo.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SaleFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Venda abate o estoque e uma venda inválida desfaz tudo (rollback)")
    void shouldDecreaseStockAndRollbackWholeSaleOnFailure() throws Exception {
        long mugId = createProduct("Caneca Teste", "39.90", 5);
        long bagId = createProduct("Ecobag Teste", "29.90", 1);

        sell("""
                        {"items": [{"productId": %d, "quantity": 3}]}
                        """.formatted(mugId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(119.70));
        assertStock(mugId, 2);

        sell("""
                        {"items": [{"productId": %d, "quantity": 1}, {"productId": %d, "quantity": 2}]}
                        """.formatted(mugId, bagId)).andExpect(status().isUnprocessableContent());

        assertStock(mugId, 2);
        assertStock(bagId, 1);
    }

    private long createProduct(String name, String price, int stock) throws Exception {
        String body = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "%s", "price": %s, "stockQuantity": %d}
                                """.formatted(name, price, stock)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private org.springframework.test.web.servlet.ResultActions sell(String json) throws Exception {
        return mockMvc.perform(
                post("/api/v1/sales").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private void assertStock(long productId, int expectedStock) throws Exception {
        mockMvc.perform(get("/api/v1/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(expectedStock));
    }
}
