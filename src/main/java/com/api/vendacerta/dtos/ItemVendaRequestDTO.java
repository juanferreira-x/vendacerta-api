package com.api.vendacerta.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemVendaRequestDTO(
        @NotNull Long produtoId,
        @NotNull @Positive int quantidade
) {
}
