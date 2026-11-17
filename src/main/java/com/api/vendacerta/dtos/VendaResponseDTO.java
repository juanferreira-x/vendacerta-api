package com.api.vendacerta.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendaResponseDTO(
        Long id,
        LocalDateTime dataVenda,
        BigDecimal valorTotal,
        List<ItemVendaResponseDTO> itens
        )
    {
}
