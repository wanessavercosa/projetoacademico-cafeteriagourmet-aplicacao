package com.chouchougourmet.projeto.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record CadastroKitRequest(

        @NotBlank
        String nome,

        String descricao,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal preco,

        @NotEmpty
        List<ItemKitRequest> componentes

) {

    public record ItemKitRequest(

            @NotNull
            Long produtoId,

            @NotNull
            @Positive
            Integer quantidade

    ) {
    }
}
