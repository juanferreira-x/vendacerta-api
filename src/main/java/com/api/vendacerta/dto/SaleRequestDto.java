package com.api.vendacerta.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Corpo da requisição para criar uma venda.
 * O {@code List<@Valid ...>} faz a validação descer para cada item; sem ele,
 * as validações dos itens seriam ignoradas.
 */
public record SaleRequestDto(
        @NotEmpty(message = "{sale.items.not-empty}") List<@Valid SaleItemRequestDto> items) {}
