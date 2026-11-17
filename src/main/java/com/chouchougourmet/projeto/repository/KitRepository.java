package com.chouchougourmet.projeto.repository;

import com.chouchougourmet.projeto.model.entity.Kit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KitRepository extends JpaRepository<Kit, Long> {

    List<Kit> findByAtivoTrue();
}

