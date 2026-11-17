package com.chouchougourmet.projeto.repository;

import com.chouchougourmet.projeto.model.entity.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {

    Optional<Carrinho> findByClienteId(Long clienteId);
}

