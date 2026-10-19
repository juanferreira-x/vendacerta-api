package com.api.vendacerta.dtos;

import java.math.BigDecimal;

public record ProdutoRequestDTO(String nome, BigDecimal preco, int quantidadeEstoque) {
}
