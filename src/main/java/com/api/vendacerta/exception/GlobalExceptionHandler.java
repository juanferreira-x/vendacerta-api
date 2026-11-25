package com.api.vendacerta.exception;

import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Tradutor central de erros: toda exceção vira uma resposta ProblemDetail (RFC 7807),
 * para que qualquer cliente (Web, Mobile, Sistemas) trate erros de um único jeito.
 *
 * <p>Herda de {@link ResponseEntityExceptionHandler}, que já converte as exceções do próprio
 * Spring MVC (JSON malformado, método HTTP não suportado, etc.). Todos os títulos e detalhes
 * vêm do {@code messages.properties}: nenhum texto ao usuário fica fixo no código.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String TIMESTAMP_PROPERTY = "timestamp";
    private static final String ERRORS_PROPERTY = "errors";

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        return buildProblem(HttpStatus.NOT_FOUND, "error.resource-not-found.title", ex);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ProblemDetail handleInsufficientStock(InsufficientStockException ex) {
        return buildProblem(HttpStatus.UNPROCESSABLE_CONTENT, "error.insufficient-stock.title", ex);
    }

    @ExceptionHandler(InvalidSortException.class)
    public ProblemDetail handleInvalidSort(InvalidSortException ex) {
        return buildProblem(HttpStatus.BAD_REQUEST, "error.invalid-parameter.title", ex);
    }

    /**
     * Duas operações alteraram o mesmo registro ao mesmo tempo (lock otimista, campo {@code @Version})
     * → 409. Nada foi salvo pela operação recusada, então o cliente pode simplesmente tentar de novo.
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail handleConcurrentUpdate(OptimisticLockingFailureException ex) {
        log.warn("Conflito de concorrência: {}", ex.getMessage());
        return buildProblem(HttpStatus.CONFLICT, "error.concurrent-update.title", "error.concurrent-update.detail");
    }

    /**
     * Rede de segurança para qualquer erro não previsto → 500 genérico.
     * O detalhe técnico (classe, SQL, stack trace) vai só para o log do servidor e NUNCA para o
     * cliente: expor isso ajudaria um atacante a entender o sistema por dentro.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Erro inesperado", ex);
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "error.unexpected.title", "error.unexpected.detail");
    }

    /**
     * Falha no {@code @Valid} → 400 com a lista de campos inválidos, para o cliente
     * destacar cada um no formulário. Sobrescreve só esta etapa da classe-pai
     * (Template Method); título e detalhe vêm do {@code messages.properties}.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ProblemDetail problem = ex.updateAndGetBody(messageSource, LocaleContextHolder.getLocale());

        List<InvalidField> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new InvalidField(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        problem.setProperty(ERRORS_PROPERTY, errors);
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    /**
     * Última etapa de todo erro tratado pela classe-pai: adiciona o timestamp para que
     * os erros do próprio Spring tenham os mesmos campos que os nossos erros de domínio.
     */
    @Override
    protected ResponseEntity<Object> createResponseEntity(
            Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

        if (body instanceof ProblemDetail problem) {
            problem.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        }
        return super.createResponseEntity(body, headers, statusCode, request);
    }

    /** Erro de domínio: o detalhe vem da chave e dos argumentos guardados na própria exceção. */
    private ProblemDetail buildProblem(HttpStatus status, String titleKey, LocalizedException ex) {
        return problem(status, translate(titleKey), translate(ex.getMessageKey(), ex.getMessageArgs()));
    }

    /** Erro com texto fixo: título e detalhe são duas chaves do messages.properties. */
    private ProblemDetail buildProblem(HttpStatus status, String titleKey, String detailKey) {
        return problem(status, translate(titleKey), translate(detailKey));
    }

    /**
     * Devolver ProblemDetail faz o Spring definir sozinho o status HTTP, o
     * Content-Type {@code application/problem+json} e o campo {@code instance}.
     */
    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        return problem;
    }

    private String translate(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    /**
     * Um campo que falhou na validação, no formato devolvido ao cliente.
     *
     * @param field   caminho do campo no corpo da requisição (ex: {@code items[0].quantity})
     * @param message mensagem de validação já traduzida
     */
    public record InvalidField(String field, String message) {}
}
