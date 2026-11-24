package com.api.vendacerta.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Corpo da requisição para criar um produto.
 * As mensagens de validação são chaves do {@code messages.properties}.
 *
 * <p>Os limites ({@code @Size} e {@code @Digits}) espelham as colunas do banco: sem eles, um valor
 * grande demais só seria barrado pelo banco, gerando um erro 500 em vez de um 400 claro.</p>
 */
public record ProductRequestDto(
        @NotBlank(message = "{product.name.not-blank}") @Size(max = 120, message = "{product.name.size}")
        String name,

        @NotNull(message = "{product.price.not-null}")
        @PositiveOrZero(message = "{product.price.positive-or-zero}")
        @Digits(integer = 10, fraction = 2, message = "{product.price.digits}")
        BigDecimal price,

        @NotNull(message = "{product.stock-quantity.not-null}")
        @PositiveOrZero(message = "{product.stock-quantity.positive-or-zero}")
        Integer stockQuantity) {}
