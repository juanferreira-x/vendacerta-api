package com.api.vendacerta.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProdutoRequestDTO(
        @NotBlank(message = "O nome do produto não pode ser vazio!")
        String nome,

        @NotNull(message = "O preço é obrigatório!")
        @PositiveOrZero(message = "O preço não pode ser negativo!")
        BigDecimal preco,

        @NotNull(message = "A quantidade em estoque é obrigatória!")
        @PositiveOrZero(message = "A quantidade em estoque não pode ser negativa!")
        int quantidadeEstoque) {
}
