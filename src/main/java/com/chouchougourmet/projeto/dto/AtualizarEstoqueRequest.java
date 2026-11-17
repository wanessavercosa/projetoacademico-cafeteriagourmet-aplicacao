package com.chouchougourmet.projeto.dto;

import jakarta.validation.constraints.NotNull;

public record AtualizarEstoqueRequest(

        @NotNull
        Integer quantidade

) {
}