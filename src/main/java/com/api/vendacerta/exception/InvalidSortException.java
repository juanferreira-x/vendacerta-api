package com.api.vendacerta.exception;

import java.util.Collection;

/**
 * Lançada quando o cliente pede ordenação por um campo fora da lista permitida. Vira HTTP 400.
 *
 * <p>A lista permitida (allowlist) impede ordenar por campos internos (como {@code version}) ou
 * inexistentes, que antes causariam um erro 500 vindo do banco.</p>
 */
public class InvalidSortException extends LocalizedException {

    private static final String MESSAGE_KEY = "pagination.invalid-sort";

    public InvalidSortException(String requestedField, Collection<String> allowedFields) {
        super(MESSAGE_KEY, requestedField, String.join(", ", allowedFields));
    }
}
