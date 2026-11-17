package com.chouchougourmet.projeto.dto;

import java.math.BigDecimal;

public record ProdutoResponseDTO(
        Long id,
        Long categoriaId,
        String categoriaNome,
        String nome,
        String descricao,
        BigDecimal preco,
        Integer quantidadeEstoque,
        Boolean ativo
) {
}
