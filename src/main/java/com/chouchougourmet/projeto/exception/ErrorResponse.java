package com.chouchougourmet.projeto.exception;

import java.time.LocalDateTime;

public record ErrorResponse(

        LocalDateTime timestamp,
        Integer status,
        String error,
        String mensagem,
        String path

) {
}