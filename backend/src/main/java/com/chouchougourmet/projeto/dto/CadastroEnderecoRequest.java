package com.chouchougourmet.projeto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CadastroEnderecoRequest(

        @NotNull
        Long clienteId,

        @NotBlank
        String rua,

        @NotBlank
        String numero,

        String complemento,

        @NotBlank
        String bairro,

        @NotBlank
        String cidade,

        @NotBlank
        String cep,

        boolean definirComoPadrao

) {
}
