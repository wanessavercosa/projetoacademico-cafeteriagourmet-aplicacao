package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.model.entity.Categoria;
import com.chouchougourmet.projeto.model.entity.Produto;
import com.chouchougourmet.projeto.repository.CategoriaRepository;
import com.chouchougourmet.projeto.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    // Listar produtos ativos
    @Transactional(readOnly = true)
    public List<Produto> listarAtivos() {
        return produtoRepository.findByAtivoTrue();
    }

    // Buscar produto pelo ID
    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Produto não encontrado."
                        )
                );
    }

    // Cadastrar produto
    @Transactional
    public Produto cadastrar(Produto dados) {

        validarProduto(dados);

        Categoria categoria = buscarCategoria(
                dados.getCategoria().getId()
        );

        dados.setCategoria(categoria);
        dados.setAtivo(true);

        return produtoRepository.save(dados);
    }

    // Atualizar produto
    @Transactional
    public Produto atualizar(Long id, Produto dados) {

        validarProduto(dados);

        Produto produto = buscarPorId(id);

        Categoria categoria = buscarCategoria(
                dados.getCategoria().getId()
        );

        produto.setNome(dados.getNome());
        produto.setDescricao(dados.getDescricao());
        produto.setPreco(dados.getPreco());
        produto.setCategoria(categoria);

        return produtoRepository.save(produto);
    }

    // Atualizar estoque
    @Transactional
    public Produto atualizarEstoque(Long id, Integer quantidade) {

        if (quantidade == null) {
            throw new IllegalArgumentException(
                    "A quantidade é obrigatória."
            );
        }

        Produto produto = buscarPorId(id);

        int novoEstoque = produto.getQuantidadeEstoque() + quantidade;

        if (novoEstoque < 0) {
            throw new IllegalArgumentException(
                    "Estoque insuficiente."
            );
        }

        produto.setQuantidadeEstoque(novoEstoque);

        return produtoRepository.save(produto);
    }

    // Inativar produto
    @Transactional
    public void inativar(Long id) {

        Produto produto = buscarPorId(id);

        produto.setAtivo(false);

        produtoRepository.save(produto);
    }

    // Validar dados do produto
    private void validarProduto(Produto produto) {

        if (produto.getNome() == null ||
                produto.getNome().isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do produto é obrigatório."
            );
        }

        if (produto.getPreco() == null ||
                produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O preço deve ser maior que zero."
            );
        }

        if (produto.getQuantidadeEstoque() == null ||
                produto.getQuantidadeEstoque() < 0) {
            throw new IllegalArgumentException(
                    "O estoque não pode ser negativo."
            );
        }

        if (produto.getCategoria() == null ||
                produto.getCategoria().getId() == null) {
            throw new IllegalArgumentException(
                    "A categoria é obrigatória."
            );
        }
    }

    // Buscar categoria
    private Categoria buscarCategoria(Long id) {

        return categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Categoria não encontrada."
                        )
                );
    }
}

