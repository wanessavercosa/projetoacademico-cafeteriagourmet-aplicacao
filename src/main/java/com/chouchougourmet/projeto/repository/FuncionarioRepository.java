package com.chouchougourmet.projeto.repository;

import com.chouchougourmet.projeto.model.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
}

