package com.api.vendacerta.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.api.vendacerta.dto.PageResponseDto;
import com.api.vendacerta.dto.ProductResponseDto;
import com.api.vendacerta.exception.InvalidSortException;
import com.api.vendacerta.exception.ResourceNotFoundException;
import com.api.vendacerta.service.ProductService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testes da camada HTTP de produtos: status, JSON, validação e formato dos erros.
 * {@code @WebMvcTest} sobe só controllers e o tratamento de erros; o service é um dublê.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    private static final String PRODUCTS_URL = "/api/v1/products";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("POST válido devolve 201 com header Location e o produto criado")
    void shouldCreateProductAndReturnLocation() throws Exception {
        when(productService.create(any()))
                .thenReturn(new ProductResponseDto(7L, "Caneca", new BigDecimal("39.90"), 10));

        mockMvc.perform(post(PRODUCTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Caneca", "price": 39.90, "stockQuantity": 10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/v1/products/7")))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Caneca"));
    }

    @Test
    @DisplayName("POST inválido devolve 400 com a lista de campos e mensagens em pt-BR")
    void shouldReturnFieldErrorsWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post(PRODUCTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "", "price": 1.234, "stockQuantity": -1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Dados inválidos"))
                .andExpect(jsonPath("$.errors.length()").value(3))
                .andExpect(jsonPath("$.errors[?(@.field == 'price')].message")
                        .value("O preço deve ter no máximo 10 dígitos inteiros e 2 casas decimais."));
    }

    @Test
    @DisplayName("Nome longo demais vira 400 (antes virava 500 com SQL vazado)")
    void shouldRejectTooLongNameWithBadRequest() throws Exception {
        String tooLongName = "a".repeat(121);

        mockMvc.perform(post(PRODUCTS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"" + tooLongName + "\", \"price\": 1, \"stockQuantity\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(
                        jsonPath("$.errors[0].message").value("O nome do produto deve ter no máximo 120 caracteres."));
    }

    @Test
    @DisplayName("Produto inexistente devolve 404 em ProblemDetail")
    void shouldReturnNotFoundForUnknownProduct() throws Exception {
        when(productService.findById(99L)).thenThrow(new ResourceNotFoundException("product.not-found", 99L));

        mockMvc.perform(get(PRODUCTS_URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.detail").value("Produto não encontrado com o ID: 99."))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Erro inesperado devolve 500 genérico sem vazar detalhes internos")
    void shouldHideInternalDetailsOnUnexpectedError() throws Exception {
        when(productService.findAll(any())).thenThrow(new IllegalStateException("SELECT * FROM products -- segredo"));

        mockMvc.perform(get(PRODUCTS_URL))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Erro interno"))
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(content().string(not(containsString("SELECT"))));
    }

    @Test
    @DisplayName("Sem parâmetros, pede a página 0 com 20 itens ordenados por id")
    void shouldUseDefaultPagination() throws Exception {
        when(productService.findAll(any())).thenReturn(new PageResponseDto<>(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get(PRODUCTS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        Pageable requested = capturePageable();
        assertThat(requested.getPageNumber()).isZero();
        assertThat(requested.getPageSize()).isEqualTo(20);
        assertThat(requested.getSort().getOrderFor("id")).isNotNull();
    }

    @Test
    @DisplayName("Página gigante é limitada a 50 itens (proteção contra abuso)")
    void shouldCapPageSizeAtFifty() throws Exception {
        when(productService.findAll(any())).thenReturn(new PageResponseDto<>(List.of(), 0, 50, 0, 0));

        mockMvc.perform(get(PRODUCTS_URL).param("size", "1000000")).andExpect(status().isOk());

        assertThat(capturePageable().getPageSize()).isEqualTo(50);
    }

    @Test
    @DisplayName("Ordenação por campo não permitido devolve 400")
    void shouldRejectUnknownSortField() throws Exception {
        when(productService.findAll(any())).thenThrow(new InvalidSortException("version", List.of("id", "name")));

        mockMvc.perform(get(PRODUCTS_URL).param("sort", "version,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Parâmetro inválido"))
                .andExpect(jsonPath("$.detail")
                        .value("Não é possível ordenar por 'version'. Campos permitidos: id, name."));
    }

    private Pageable capturePageable() {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(productService).findAll(captor.capture());
        return captor.getValue();
    }
}
