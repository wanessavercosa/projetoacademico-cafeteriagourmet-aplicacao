package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.model.entity.Categoria;
import com.chouchougourmet.projeto.model.entity.Produto;
import com.chouchougourmet.projeto.repository.CategoriaRepository;
import com.chouchougourmet.projeto.repository.ProdutoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void deveCadastrarProdutoComSucesso() {

        Categoria categoria = new Categoria();
        categoria.setId(1L);

        Produto produto = new Produto();
        produto.setNome("Café Expresso");
        produto.setPreco(new BigDecimal("5.50"));
        produto.setQuantidadeEstoque(10);
        produto.setCategoria(categoria);

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(produtoRepository.save(any()))
                .thenReturn(produto);

        Produto resultado =
                produtoService.cadastrar(produto);

        assertNotNull(resultado);

        verify(produtoRepository)
                .save(any());
    }

    @Test
    void deveLancarErroQuandoNomeForVazio() {

        Categoria categoria = new Categoria();
        categoria.setId(1L);

        Produto produto = new Produto();
        produto.setNome("");
        produto.setPreco(new BigDecimal("5.50"));
        produto.setQuantidadeEstoque(10);
        produto.setCategoria(categoria);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> produtoService.cadastrar(produto)
                );

        assertEquals(
                "O nome do produto é obrigatório.",
                exception.getMessage()
        );

        verify(produtoRepository, never())
                .save(any());
    }
}