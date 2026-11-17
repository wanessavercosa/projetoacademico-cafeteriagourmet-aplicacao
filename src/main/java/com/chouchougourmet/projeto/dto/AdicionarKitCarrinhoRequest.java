package com.chouchougourmet.projeto.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AdicionarKitCarrinhoRequest(

        @NotNull
        Long kitId,

        @Positive
        Integer quantidade
) {
}
