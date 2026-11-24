package com.api.vendacerta.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.api.vendacerta.exception.InsufficientStockException;
import com.api.vendacerta.service.SaleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testes da camada HTTP de vendas: validação dos itens e tradução das exceções em status HTTP.
 */
@WebMvcTest(SaleController.class)
class SaleControllerTest {

    private static final String SALES_URL = "/api/v1/sales";
    private static final String VALID_SALE = """
            {"items": [{"productId": 1, "quantity": 2}]}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleService saleService;

    @Test
    @DisplayName("Quantidade negativa em um item devolve 400 apontando o item")
    void shouldRejectNegativeQuantity() throws Exception {
        mockMvc.perform(post(SALES_URL).contentType(MediaType.APPLICATION_JSON).content("""
                                {"items": [{"productId": 1, "quantity": -1}]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("items[0].quantity"));
    }

    @Test
    @DisplayName("Venda sem itens devolve 400")
    void shouldRejectEmptySale() throws Exception {
        mockMvc.perform(post(SALES_URL).contentType(MediaType.APPLICATION_JSON).content("{\"items\": []}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("items"));
    }

    @Test
    @DisplayName("Estoque insuficiente devolve 422")
    void shouldReturnUnprocessableWhenStockIsInsufficient() throws Exception {
        when(saleService.create(any())).thenThrow(new InsufficientStockException("Caneca", 1, 2));

        mockMvc.perform(post(SALES_URL).contentType(MediaType.APPLICATION_JSON).content(VALID_SALE))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail")
                        .value("Estoque insuficiente para o produto: Caneca | Disponível: 1 | Solicitado: 2"));
    }

    @Test
    @DisplayName("Duas vendas simultâneas do mesmo produto: a perdedora recebe 409")
    void shouldReturnConflictOnConcurrentUpdate() throws Exception {
        when(saleService.create(any())).thenThrow(new OptimisticLockingFailureException("version mismatch"));

        mockMvc.perform(post(SALES_URL).contentType(MediaType.APPLICATION_JSON).content(VALID_SALE))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de atualização"));
    }
}
