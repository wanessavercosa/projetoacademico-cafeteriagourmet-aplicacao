package com.chouchougourmet.projeto.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProdutoRequestDTO(

        @NotNull
        Long categoriaId,

        @NotBlank
        String nome,

        String descricao,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal preco,

        @NotNull
        @PositiveOrZero
        Integer quantidadeEstoque
) {
}