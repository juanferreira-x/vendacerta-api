package com.api.vendacerta.dtos;

import com.api.vendacerta.entities.ItemVenda;
import com.api.vendacerta.entities.Venda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendaResponseDTO(
        Long id,
        LocalDateTime dataVenda,
        BigDecimal total,
        List<ItemVendaResponseDTO> itens
        )
    {
        public static VendaResponseDTO fromEntity(Venda venda){

            return new VendaResponseDTO(
            venda.getId(),
            venda.getDataVenda(),
            venda.getTotal(),
            venda.getItens().stream()
                    .map(ItemVendaResponseDTO::fromEntity)
                    .toList()
            );
        }
}
