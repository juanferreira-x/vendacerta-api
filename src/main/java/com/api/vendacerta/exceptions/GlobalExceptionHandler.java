package com.api.vendacerta.exceptions;

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

import java.time.Instant;
import java.util.List;

/**
 * Tradutor central de erros da API: toda exceção vira uma resposta no formato
 * ProblemDetail (RFC 7807), para que qualquer cliente (Web, Mobile, Sistemas)
 * trate erros de um único jeito.
 *
 * <p>Herda de {@link ResponseEntityExceptionHandler}, que já converte as exceções
 * nativas do Spring MVC (JSON malformado, método HTTP não suportado, etc.) para
 * ProblemDetail. Aqui só tratamos as exceções de domínio e personalizamos o 400.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Recurso inexistente (ex: produto com ID que não existe) → 404.
     * Retornar ProblemDetail direto faz o Spring definir o status HTTP,
     * o Content-Type application/problem+json e o campo "instance" sozinho.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());

        problema.setTitle("Recurso não encontrado");
        problema.setProperty("timestamp", Instant.now());

        return problema;
    }

    /**
     * Regra de negócio violada: venda pede mais unidades do que há em estoque → 422.
     * A requisição está bem formada, mas não pode ser atendida.
     */
    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ProblemDetail handleEstoqueInsuficiente(EstoqueInsuficienteException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());

        problema.setTitle("Estoque insuficiente");
        problema.setProperty("timestamp", Instant.now());

        return problema;
    }

    /**
     * Falha no @Valid → 400 com a lista de campos inválidos.
     * Sobrescreve só este método da classe-pai (Template Method): os demais erros
     * do Spring continuam padronizados sem precisarmos escrever nada.
     * A lista "erros" permite ao cliente destacar cada campo no formulário.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        ProblemDetail problema = ex.getBody(); // a classe-pai já montou um ProblemDetail 400
        problema.setTitle("Dados inválidos");
        problema.setDetail("Um ou mais campos estão inválidos.");
        problema.setProperty("timestamp", Instant.now());

        List<CampoInvalido> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> new CampoInvalido(erro.getField(), erro.getDefaultMessage()))
                .toList();

        problema.setProperty("erros", erros);
        return handleExceptionInternal(ex, problema, headers, status, request);
    }

    /** Um campo que falhou na validação, no formato devolvido ao cliente. */
    public record CampoInvalido(String campo, String mensagem) {
    }
}
