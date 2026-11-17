package com.chouchougourmet.projeto.repository;

import com.chouchougourmet.projeto.model.entity.Pedido;
import com.chouchougourmet.projeto.model.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByClienteIdAndStatus(
            Long clienteId, StatusPedido status);

    Optional<Pedido> findByNumeroPedido(Integer numeroPedido);
}

