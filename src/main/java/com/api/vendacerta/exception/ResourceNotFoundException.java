package com.api.vendacerta.exception;

/**
 * Lançada quando o recurso solicitado não existe. Vira HTTP 404.
 *
 * <p>Genérica de propósito: quem lança escolhe a chave da mensagem, então a mesma
 * exceção serve para produtos, vendas e qualquer recurso futuro.</p>
 */
public class ResourceNotFoundException extends LocalizedException {

    public ResourceNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }
}
