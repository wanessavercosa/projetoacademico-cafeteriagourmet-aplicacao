package com.chouchougourmet.projeto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record ValidarCupomRequest(

        @NotBlank
        String codigo,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal valorPedido

) {
}

