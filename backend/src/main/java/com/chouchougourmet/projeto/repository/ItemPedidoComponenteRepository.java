package com.chouchougourmet.projeto.repository;

import com.chouchougourmet.projeto.model.entity.ItemPedidoComponente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemPedidoComponenteRepository
        extends JpaRepository<ItemPedidoComponente, Long> {

    List<ItemPedidoComponente> findByItemPedidoId(Long itemPedidoId);
}

