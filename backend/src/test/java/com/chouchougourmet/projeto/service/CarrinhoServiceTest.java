package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.model.entity.Carrinho;
import com.chouchougourmet.projeto.model.entity.Cliente;
import com.chouchougourmet.projeto.model.entity.ItemCarrinho;
import com.chouchougourmet.projeto.model.entity.Produto;
import com.chouchougourmet.projeto.repository.CarrinhoRepository;
import com.chouchougourmet.projeto.repository.ClienteRepository;
import com.chouchougourmet.projeto.repository.ItemCarrinhoRepository;
import com.chouchougourmet.projeto.repository.KitRepository;
import com.chouchougourmet.projeto.repository.ProdutoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarrinhoServiceTest {

    @Mock
    private CarrinhoRepository carrinhoRepository;

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private KitRepository kitRepository;

    @InjectMocks
    private CarrinhoService carrinhoService;

    @Test
    void deveAdicionarProdutoAoCarrinho() {

        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Produto produto = new Produto();
        produto.setId(10L);
        produto.setNome("Café Expresso");
        produto.setPreco(new BigDecimal("8.50"));
        produto.setQuantidadeEstoque(20);
        produto.setAtivo(true);

        Carrinho carrinho = new Carrinho();
        carrinho.setId(100L);
        carrinho.setCliente(cliente);

        when(produtoRepository.findById(10L))
                .thenReturn(Optional.of(produto));

        when(carrinhoRepository.findByClienteId(1L))
                .thenReturn(Optional.of(carrinho));

        when(itemCarrinhoRepository.findByCarrinhoId(100L))
                .thenReturn(List.of());

        when(itemCarrinhoRepository.save(any()))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        ItemCarrinho item =
                carrinhoService.adicionarProduto(
                        1L,
                        10L,
                        2
                );

        assertNotNull(item);

        assertEquals(
                2,
                item.getQuantidade()
        );

        assertEquals(
                produto,
                item.getProduto()
        );

        verify(itemCarrinhoRepository)
                .save(any(ItemCarrinho.class));
    }

    @Test
    void deveLancarErroQuandoEstoqueForInsuficiente() {

        Produto produto = new Produto();

        produto.setId(10L);
        produto.setNome("Café Expresso");
        produto.setPreco(new BigDecimal("8.50"));
        produto.setQuantidadeEstoque(2);
        produto.setAtivo(true);

        when(produtoRepository.findById(10L))
                .thenReturn(Optional.of(produto));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> carrinhoService.adicionarProduto(
                                1L,
                                10L,
                                10
                        )
                );

        assertEquals(
                "Estoque insuficiente.",
                exception.getMessage()
        );

        verify(itemCarrinhoRepository, never())
                .save(any());
    }

    @Test
    void deveLancarErroQuandoQuantidadeForInvalida() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> carrinhoService.adicionarProduto(
                                1L,
                                10L,
                                0
                        )
                );

        assertEquals(
                "A quantidade deve ser maior que zero.",
                exception.getMessage()
        );
    }
}