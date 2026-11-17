package com.chouchougourmet.projeto.dto;

public record EnderecoResponseDTO(
        Long id,
        Long clienteId,
        String rua,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String cep,
        boolean padrao
) {
}