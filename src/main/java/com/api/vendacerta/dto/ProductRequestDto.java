package com.api.vendacerta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * Corpo da requisição para criar um produto.
 * As mensagens de validação são chaves do {@code messages.properties}.
 */
public record ProductRequestDto(
        @NotBlank(message = "{product.name.not-blank}")
        String name,

        @NotNull(message = "{product.price.not-null}")
        @PositiveOrZero(message = "{product.price.positive-or-zero}")
        BigDecimal price,

        @NotNull(message = "{product.stock-quantity.not-null}")
        @PositiveOrZero(message = "{product.stock-quantity.positive-or-zero}")
        Integer stockQuantity
) {
}
