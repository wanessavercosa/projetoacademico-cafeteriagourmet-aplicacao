package com.chouchougourmet.projeto.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoResponseDTO(

        Long id,
        Integer numeroPedido,
        String status,
        LocalDateTime dataPedido,
        BigDecimal valorItens,
        BigDecimal valorDesconto,
        BigDecimal taxaEntrega,
        BigDecimal valorTotal

) {
}