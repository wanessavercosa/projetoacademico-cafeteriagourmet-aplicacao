package com.chouchougourmet.projeto.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FinalizarPedidoRequest {

        @NotNull(message = "O endereço é obrigatório.")
        private Long enderecoId;

        private String codigoCupom;

        @NotNull(message = "A distância é obrigatória.")
        @DecimalMin(value = "0.0",
                message = "A distância não pode ser negativa.")
        private BigDecimal distanciaKm;
}


