package com.chouchougourmet.projeto.controller;

import com.chouchougourmet.projeto.dto.AdicionarCarrinhoRequest;
import com.chouchougourmet.projeto.dto.AdicionarKitCarrinhoRequest;
import com.chouchougourmet.projeto.model.entity.ItemCarrinho;
import com.chouchougourmet.projeto.service.CarrinhoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carrinhos")
@RequiredArgsConstructor
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ItemCarrinho>> listarItens(
            @PathVariable Long clienteId) {

        return ResponseEntity.ok(
                carrinhoService.listarItens(clienteId)
        );
    }

    @PostMapping("/cliente/{clienteId}/produto")
    public ResponseEntity<ItemCarrinho> adicionarProduto(
            @PathVariable Long clienteId,
            @Valid @RequestBody
            AdicionarCarrinhoRequest dados) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        carrinhoService.adicionarProduto(
                                clienteId,
                                dados.produtoId(),
                                dados.quantidade()
                        )
                );
    }

    @PostMapping("/cliente/{clienteId}/kit")
    public ResponseEntity<ItemCarrinho> adicionarKit(
            @PathVariable Long clienteId,
            @Valid @RequestBody
            AdicionarKitCarrinhoRequest dados) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        carrinhoService.adicionarKit(
                                clienteId,
                                dados.kitId(),
                                dados.quantidade()
                        )
                );
    }

    @DeleteMapping("/cliente/{clienteId}/item/{itemId}")
    public ResponseEntity<Void> removerItem(
            @PathVariable Long clienteId,
            @PathVariable Long itemId) {

        carrinhoService.removerItem(
                clienteId,
                itemId
        );

        return ResponseEntity.noContent().build();
    }
}