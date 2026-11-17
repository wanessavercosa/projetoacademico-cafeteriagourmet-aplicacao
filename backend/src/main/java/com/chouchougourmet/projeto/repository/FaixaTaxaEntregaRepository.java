package com.chouchougourmet.projeto.repository;

import com.chouchougourmet.projeto.model.entity.FaixaTaxaEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FaixaTaxaEntregaRepository
        extends JpaRepository<FaixaTaxaEntrega, Long> {

    List<FaixaTaxaEntrega> findByAtivoTrue();
}

