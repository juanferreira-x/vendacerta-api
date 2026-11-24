package com.api.vendacerta.dto;

import com.api.vendacerta.entity.Product;
import java.math.BigDecimal;

/**
 * Representação pública do produto. Desacopla o contrato da API da entidade {@link Product}.
 */
public record ProductResponseDto(Long id, String name, BigDecimal price, Integer stockQuantity) {

    public static ProductResponseDto fromEntity(Product product) {
        return new ProductResponseDto(
                product.getId(), product.getName(), product.getPrice(), product.getStockQuantity());
    }
}
