package com.chouchougourmet.projeto.repository;

import com.chouchougourmet.projeto.model.entity.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
}
