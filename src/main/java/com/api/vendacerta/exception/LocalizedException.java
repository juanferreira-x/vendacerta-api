package com.api.vendacerta.exception;

/**
 * Classe-mãe das exceções de domínio cuja mensagem ao usuário fica no {@code messages.properties}.
 *
 * <p>A exceção carrega só a chave da mensagem e seus argumentos, nunca o texto final.
 * Assim o domínio não se mistura com apresentação: o {@link GlobalExceptionHandler}
 * traduz a chave para o idioma do cliente ao montar a resposta HTTP.</p>
 */
public abstract class LocalizedException extends RuntimeException {

    private final String messageKey;
    private final transient Object[] messageArgs;

    protected LocalizedException(String messageKey, Object... messageArgs) {
        super(messageKey);
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getMessageArgs() {
        return messageArgs.clone();
    }
}
