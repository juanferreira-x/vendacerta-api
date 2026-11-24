package com.api.vendacerta.dto;

import com.api.vendacerta.entity.Sale;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Representação pública de uma venda concluída.
 */
public record SaleResponseDto(Long id, Instant createdAt, BigDecimal total, List<SaleItemResponseDto> items) {

    /**
     * Deve ser chamado dentro da transação: {@code items} é carregado sob demanda (lazy).
     */
    public static SaleResponseDto fromEntity(Sale sale) {
        return new SaleResponseDto(
                sale.getId(),
                sale.getCreatedAt(),
                sale.getTotal(),
                sale.getItems().stream().map(SaleItemResponseDto::fromEntity).toList());
    }
}
