package com.api.vendacerta.dto;

import com.api.vendacerta.entity.SaleItem;

import java.math.BigDecimal;

/**
 * Representação pública de um item da venda. Não aponta de volta para a venda,
 * o que quebra o ciclo Sale ↔ SaleItem que causava loop infinito no JSON.
 */
public record SaleItemResponseDto(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {

    public static SaleItemResponseDto fromEntity(SaleItem item) {
        return new SaleItemResponseDto(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}
