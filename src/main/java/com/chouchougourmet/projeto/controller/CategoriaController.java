package com.chouchougourmet.projeto.controller;

import com.chouchougourmet.projeto.model.entity.Categoria;
import com.chouchougourmet.projeto.service.CategoriaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<Categoria>> listarTodas() {

        return ResponseEntity.ok(
                categoriaService.listarTodas()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                categoriaService.buscarPorId(id)
        );
    }

    @PostMapping
    public ResponseEntity<Categoria> cadastrar(
            @RequestBody Categoria categoria) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        categoriaService.cadastrar(categoria)
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizar(
            @PathVariable Long id,
            @RequestBody Categoria categoria) {

        return ResponseEntity.ok(
                categoriaService.atualizar(
                        id,
                        categoria
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        categoriaService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}