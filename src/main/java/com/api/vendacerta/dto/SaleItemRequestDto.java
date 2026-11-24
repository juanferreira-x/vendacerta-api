package com.api.vendacerta.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Um item do pedido de venda: qual produto e quantas unidades.
 * O {@code @Positive} bloqueia quantidades negativas, que aumentariam o estoque.
 */
public record SaleItemRequestDto(
        @NotNull(message = "{sale.item.product-id.not-null}")
        Long productId,

        @NotNull(message = "{sale.item.quantity.not-null}") @Positive(message = "{sale.item.quantity.positive}")
        Integer quantity) {}
