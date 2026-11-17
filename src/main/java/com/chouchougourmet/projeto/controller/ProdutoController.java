package com.chouchougourmet.projeto.controller;

import com.chouchougourmet.projeto.dto.AtualizarEstoqueRequest;
import com.chouchougourmet.projeto.dto.ProdutoRequestDTO;
import com.chouchougourmet.projeto.dto.ProdutoResponseDTO;
import com.chouchougourmet.projeto.model.entity.Categoria;
import com.chouchougourmet.projeto.model.entity.Produto;
import com.chouchougourmet.projeto.service.ProdutoService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarAtivos() {

        List<ProdutoResponseDTO> produtos =
                produtoService.listarAtivos()
                        .stream()
                        .map(this::converterParaDTO)
                        .toList();

        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(
            @PathVariable Long id) {

        Produto produto = produtoService.buscarPorId(id);

        return ResponseEntity.ok(
                converterParaDTO(produto)
        );
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrar(
            @Valid @RequestBody ProdutoRequestDTO dados) {

        Produto produto = converterParaEntidade(dados);

        Produto cadastrado =
                produtoService.cadastrar(produto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        converterParaDTO(cadastrado)
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequestDTO dados) {

        Produto produto = converterParaEntidade(dados);

        Produto atualizado =
                produtoService.atualizar(id, produto);

        return ResponseEntity.ok(
                converterParaDTO(atualizado)
        );
    }


    @PatchMapping("/{id}/estoque")
    public ResponseEntity<ProdutoResponseDTO> atualizarEstoque(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarEstoqueRequest dados) {

        Produto produto =
                produtoService.atualizarEstoque(
                        id,
                        dados.quantidade()
                );

        return ResponseEntity.ok(
                converterParaDTO(produto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(
            @PathVariable Long id) {

        produtoService.inativar(id);

        return ResponseEntity.noContent().build();
    }

    private Produto converterParaEntidade(
            ProdutoRequestDTO dados) {

        Categoria categoria = new Categoria();

        categoria.setId(
                dados.categoriaId()
        );

        Produto produto = new Produto();

        produto.setCategoria(categoria);
        produto.setNome(dados.nome());
        produto.setDescricao(dados.descricao());
        produto.setPreco(dados.preco());
        produto.setQuantidadeEstoque(
                dados.quantidadeEstoque()
        );

        return produto;
    }

    private ProdutoResponseDTO converterParaDTO(
            Produto produto) {

        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getCategoria().getId(),
                produto.getCategoria().getNome(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidadeEstoque(),
                produto.getAtivo()
        );
    }
}