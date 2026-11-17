package com.chouchougourmet.projeto.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AdicionarCarrinhoRequest(

        @NotNull
        Long produtoId,

        @Positive
        Integer quantidade
) {
}
