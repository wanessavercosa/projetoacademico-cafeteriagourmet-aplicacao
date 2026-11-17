package com.chouchougourmet.projeto.repository;

import com.chouchougourmet.projeto.model.entity.PedidoFavorito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoFavoritoRepository
        extends JpaRepository<PedidoFavorito, Long> {

    List<PedidoFavorito> findByClienteId(Long clienteId);
}

