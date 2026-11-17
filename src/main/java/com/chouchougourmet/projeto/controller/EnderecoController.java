package com.chouchougourmet.projeto.controller;

import com.chouchougourmet.projeto.dto.CadastroEnderecoRequest;
import com.chouchougourmet.projeto.dto.EnderecoResponseDTO;
import com.chouchougourmet.projeto.model.entity.Endereco;
import com.chouchougourmet.projeto.service.EnderecoService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enderecos")
@RequiredArgsConstructor
public class EnderecoController {

    private final EnderecoService enderecoService;

    @PostMapping
    public ResponseEntity<Endereco> cadastrar(
            @Valid @RequestBody CadastroEnderecoRequest dados) {

        Endereco endereco =
                enderecoService.cadastrar(dados);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(endereco);
    }

    @GetMapping
    public ResponseEntity<List<EnderecoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(enderecoService.listarTodos());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<EnderecoResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(enderecoService.listarPorCliente(clienteId));
    }
}