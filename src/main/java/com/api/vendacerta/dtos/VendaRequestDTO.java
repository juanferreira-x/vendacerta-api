package com.api.vendacerta.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record VendaRequestDTO(
        @NotEmpty(message = "A venda deve ter pelo menos um item")
        List<@Valid ItemVendaRequestDTO> itens
) {
}
