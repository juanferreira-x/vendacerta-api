package com.api.vendacerta.exception;

/**
 * Lançada quando a venda pede mais unidades do que o produto tem em estoque. Vira HTTP 422.
 *
 * <p>É unchecked (RuntimeException) de propósito: o {@code @Transactional} só faz rollback
 * automático nesse tipo de exceção, então a falha em qualquer item desfaz o estoque já abatido.</p>
 */
public class InsufficientStockException extends LocalizedException {

    private static final String MESSAGE_KEY = "sale.insufficient-stock";

    public InsufficientStockException(String productName, int availableQuantity, int requestedQuantity) {
        super(MESSAGE_KEY, productName, availableQuantity, requestedQuantity);
    }
}
