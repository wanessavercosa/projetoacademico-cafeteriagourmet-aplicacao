package com.chouchougourmet.projeto.repository;

import com.chouchougourmet.projeto.model.entity.ConfiguracaoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ConfiguracaoEntregaRepository
        extends JpaRepository<ConfiguracaoEntrega, Long> {

    Optional<ConfiguracaoEntrega> findByAtivoTrue();
}

