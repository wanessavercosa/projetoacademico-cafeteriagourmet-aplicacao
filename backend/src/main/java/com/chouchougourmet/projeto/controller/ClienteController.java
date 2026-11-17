package com.chouchougourmet.projeto.controller;

import com.chouchougourmet.projeto.dto.CadastroClienteRequest;
import com.chouchougourmet.projeto.dto.LoginRequest;
import com.chouchougourmet.projeto.model.entity.Cliente;
import com.chouchougourmet.projeto.service.ClienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> cadastrar(
            @Valid @RequestBody CadastroClienteRequest dados) {

        Cliente cliente =
                clienteService.cadastrar(dados);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cliente);
    }

    @PostMapping("/login")
    public ResponseEntity<Cliente> login(
            @RequestBody LoginRequest dados) {

        Cliente cliente =
                clienteService.login(
                        dados.email(),
                        dados.senha()
                );

        return ResponseEntity.ok(cliente);
    }
}
