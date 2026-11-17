package com.chouchougourmet.projeto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroClienteRequest(
        @NotBlank
        String nome,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @NotBlank
        @Size(min = 6, max = 100)
        String senha,

        String celular
) {
}
