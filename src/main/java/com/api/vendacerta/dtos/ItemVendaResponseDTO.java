package com.api.vendacerta.dtos;

import java.math.BigDecimal;

public record ItemVendaResponseDTO(
        Long produtoId,
        String nomeProduto,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
        )
    {
}
