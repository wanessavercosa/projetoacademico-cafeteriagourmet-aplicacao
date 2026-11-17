package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.model.entity.*;
import com.chouchougourmet.projeto.repository.*;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private CarrinhoRepository carrinhoRepository;

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @Mock
    private ItemPedidoComponenteRepository componenteRepository;

    @Mock
    private ItemKitRepository itemKitRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private KitRepository kitRepository;

    @Mock
    private CupomRepository cupomRepository;

    @Mock
    private ConfiguracaoEntregaRepository configuracaoEntregaRepository;

    @Mock
    private EntregaRepository entregaRepository;

    @Mock
    private EntregaService entregaService;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void deveLancarErroQuandoCarrinhoEstiverVazio() {

        Cliente cliente = new Cliente();
        cliente.setId(1L);

        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCliente(cliente);

        Carrinho carrinho = new Carrinho();
        carrinho.setId(1L);

        when(clienteRepository.findById(1L))
                .thenReturn(Optional.of(cliente));

        when(enderecoRepository.findById(1L))
                .thenReturn(Optional.of(endereco));

        when(carrinhoRepository.findByClienteId(1L))
                .thenReturn(Optional.of(carrinho));

        when(itemCarrinhoRepository.findByCarrinhoId(1L))
                .thenReturn(List.of());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> pedidoService.criarPedido(
                                1L,
                                1L,
                                null,
                                new BigDecimal("2")
                        )
                );

        assertEquals(
                "O carrinho está vazio.",
                exception.getMessage()
        );
    }

    @Test
    void deveLancarErroQuandoPedidoNaoExistir() {

        when(pedidoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> pedidoService.buscarPorId(999L)
        );
    }
}