package com.api.vendacerta;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.math.BigDecimal;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Paginação de ponta a ponta, com o banco H2 e os produtos de demonstração do seed (5 produtos).
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductPaginationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Devolve só o tamanho pedido, com os totais, ordenado pelo preço do maior para o menor")
    void shouldReturnRequestedPageSortedByPriceDescending() throws Exception {
        String body = mockMvc.perform(get("/api/v1/products").param("size", "2").param("sort", "price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(5)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Number> prices = JsonPath.read(body, "$.content[*].price");
        BigDecimal first = new BigDecimal(prices.get(0).toString());
        BigDecimal second = new BigDecimal(prices.get(1).toString());
        Assertions.assertThat(first).isGreaterThanOrEqualTo(second);
    }

    @Test
    @DisplayName("Ordenar por campo interno (version) devolve 400, e não 500")
    void shouldRejectInternalSortField() throws Exception {
        mockMvc.perform(get("/api/v1/products").param("sort", "version"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Parâmetro inválido"));
    }
}
