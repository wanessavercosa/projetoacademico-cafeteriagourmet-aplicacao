package com.chouchougourmet.projeto.controller;

import com.chouchougourmet.projeto.dto.CadastroKitRequest;
import com.chouchougourmet.projeto.model.entity.Kit;
import com.chouchougourmet.projeto.service.KitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kits")
@RequiredArgsConstructor
public class KitController {

    private final KitService kitService;

    @GetMapping
    public ResponseEntity<List<Kit>> listarAtivos() {

        return ResponseEntity.ok(
                kitService.listarAtivos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kit> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                kitService.buscarPorId(id)
        );
    }

    @PostMapping
    public ResponseEntity<Kit> cadastrar(
            @Valid @RequestBody CadastroKitRequest dados) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        kitService.cadastrar(dados)
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Kit> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CadastroKitRequest dados) {

        return ResponseEntity.ok(
                kitService.atualizar(id, dados)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(
            @PathVariable Long id) {

        kitService.inativar(id);

        return ResponseEntity.noContent().build();
    }
}