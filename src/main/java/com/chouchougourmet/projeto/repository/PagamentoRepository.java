package com.chouchougourmet.projeto.repository;

import com.chouchougourmet.projeto.model.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByClienteId(Long clienteId);
}

