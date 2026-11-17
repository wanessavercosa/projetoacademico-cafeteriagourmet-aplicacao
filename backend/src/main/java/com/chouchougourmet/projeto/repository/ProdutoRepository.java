package com.chouchougourmet.projeto.repository;

import com.chouchougourmet.projeto.model.entity.Produto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Override
    @EntityGraph(attributePaths = "categoria")
    Optional<Produto> findById(Long id);

    @EntityGraph(attributePaths = "categoria")
    List<Produto> findByAtivoTrue();

    List<Produto> findByCategoriaId(Long categoriaId);

    List<Produto> findByNomeContainingIgnoreCase(String nome);
}

