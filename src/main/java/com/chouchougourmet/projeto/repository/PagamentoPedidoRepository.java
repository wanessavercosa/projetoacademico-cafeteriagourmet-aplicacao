package com.chouchougourmet.projeto.repository;

import com.chouchougourmet.projeto.model.entity.PagamentoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagamentoPedidoRepository
        extends JpaRepository<PagamentoPedido, Long> {

    List<PagamentoPedido> findByPagamentoId(Long pagamentoId);

    List<PagamentoPedido> findByPedidoId(Long pedidoId);
}

