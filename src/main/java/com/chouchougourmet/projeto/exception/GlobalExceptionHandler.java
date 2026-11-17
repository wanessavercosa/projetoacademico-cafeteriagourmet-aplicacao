package com.chouchougourmet.projeto.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> tratarEntidadeNaoEncontrada(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse erro = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(erro);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> tratarRegraNegocio(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        ErrorResponse erro = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Requisição inválida",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .badRequest()
                .body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarValidacoes(
            MethodArgumentNotValidException ex) {

        Map<String, String> erros = new LinkedHashMap<>();

        for (FieldError error :
                ex.getBindingResult().getFieldErrors()) {

            erros.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        return ResponseEntity
                .badRequest()
                .body(erros);
    }

   @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> tratarErroGenerico(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse erro = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(erro);
    }
}