package com.api.vendacerta.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;


@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());

        problema.setTitle("Recurso não encontrado");
        problema.setProperty("timestamp", Instant.now());

        return problema;

    }


    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ProblemDetail handleEstoqueInsuficiente(EstoqueInsuficienteException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());

        problema.setTitle("Estoque insuficiente");
        problema.setProperty("timestamp", Instant.now());

        return problema;
    }
}
