
package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.model.entity.*;
import com.chouchougourmet.projeto.model.enums.StatusEntrega;
import com.chouchougourmet.projeto.model.enums.StatusPedido;
import com.chouchougourmet.projeto.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ItemPedidoComponenteRepository componenteRepository;
    private final ItemKitRepository itemKitRepository;
    private final ProdutoRepository produtoRepository;
    private final KitRepository kitRepository;
    private final CupomRepository cupomRepository;
    private final ConfiguracaoEntregaRepository configuracaoEntregaRepository;
    private final EntregaRepository entregaRepository;
    private final EntregaService entregaService;


    // ==========================================
    // CRIAR PEDIDO
    // ==========================================

    @Transactional
    public Pedido criarPedido(
            Long clienteId,
            Long enderecoId,
            String codigoCupom,
            BigDecimal distanciaKm) {

        validarDistancia(distanciaKm);

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Cliente não encontrado."));

        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Endereço não encontrado."));

        if (!endereco.getCliente().getId().equals(clienteId)) {
            throw new IllegalArgumentException(
                    "O endereço não pertence ao cliente.");
        }

        Carrinho carrinho = carrinhoRepository.findByClienteId(clienteId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Carrinho não encontrado."));

        List<ItemCarrinho> itensCarrinho =
                itemCarrinhoRepository.findByCarrinhoId(carrinho.getId());

        if (itensCarrinho.isEmpty()) {
            throw new IllegalArgumentException(
                    "O carrinho está vazio.");
        }

        // 1. Calcular o consumo total de estoque
        Map<Long, ConsumoEstoque> consumoEstoque =
                calcularConsumoEstoque(itensCarrinho);

        // 2. Validar estoque total antes de qualquer baixa
        validarEstoqueTotal(consumoEstoque);

        // 3. Calcular valor dos itens
        BigDecimal valorItens = calcularValorItens(itensCarrinho);

        // 4. Validar cupom
        Cupom cupom = buscarCupomValido(codigoCupom, valorItens);

        BigDecimal valorDesconto = calcularDesconto(
                cupom, valorItens);

        // 5. Calcular taxa de entrega
        BigDecimal taxaEntrega =
                entregaService.calcularTaxaEntrega(distanciaKm);

        // 6. Calcular valor total
        BigDecimal valorTotal = valorItens
                .subtract(valorDesconto)
                .add(taxaEntrega);

        // 7. Criar pedido
        Pedido pedido = new Pedido();

        pedido.setCliente(cliente);
        pedido.setEndereco(endereco);
        pedido.setCupom(cupom);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.AGUARDANDO);
        pedido.setValorItens(valorItens);
        pedido.setValorDesconto(valorDesconto);
        pedido.setTaxaEntrega(taxaEntrega);
        pedido.setValorTotal(valorTotal);
        pedido.setNumeroPedido(gerarNumeroPedido());

        pedido = pedidoRepository.save(pedido);

        // 8. Registrar os itens e snapshots dos kits
        for (ItemCarrinho item : itensCarrinho) {
            registrarItemPedido(pedido, item);
        }

        // 9. Efetuar baixa do estoque uma única vez por produto
        baixarEstoque(consumoEstoque);

        // 10. Criar entrega
        criarEntrega(pedido, distanciaKm);

        // 11. Limpar carrinho
        itemCarrinhoRepository.deleteAll(itensCarrinho);

        return pedido;
    }


    // ==========================================
    // CALCULAR CONSUMO TOTAL DO ESTOQUE
    // ==========================================

    private Map<Long, ConsumoEstoque> calcularConsumoEstoque(
            List<ItemCarrinho> itensCarrinho) {

        Map<Long, ConsumoEstoque> consumo = new LinkedHashMap<>();

        for (ItemCarrinho item : itensCarrinho) {

            if (item.getQuantidade() == null ||
                    item.getQuantidade() <= 0) {

                throw new IllegalArgumentException(
                        "Quantidade inválida no carrinho.");
            }

            boolean temProduto = item.getProduto() != null;
            boolean temKit = item.getKit() != null;

            if (temProduto == temKit) {
                throw new IllegalArgumentException(
                        "O item deve possuir um produto ou um kit.");
            }

            if (item.getPrecoUnitario() == null ||
                    item.getPrecoUnitario().compareTo(BigDecimal.ZERO) < 0) {

                throw new IllegalArgumentException(
                        "Preço inválido no carrinho.");
            }

            if (temProduto) {

                Produto produto = item.getProduto();

                validarProdutoAtivo(produto);

                adicionarConsumo(
                        consumo,
                        produto,
                        item.getQuantidade());

            } else {

                Kit kit = item.getKit();

                validarKitAtivo(kit);

                List<ItemKit> componentes =
                        itemKitRepository.findByKitId(kit.getId());

                if (componentes.isEmpty()) {
                    throw new IllegalArgumentException(
                            "O kit não possui produtos cadastrados.");
                }

                for (ItemKit componente : componentes) {

                    Produto produto = componente.getProduto();

                    validarProdutoAtivo(produto);

                    int quantidadeTotal =
                            componente.getQuantidade()
                                    * item.getQuantidade();

                    adicionarConsumo(
                            consumo,
                            produto,
                            quantidadeTotal);
                }
            }
        }

        return consumo;
    }


    // ==========================================
    // ADICIONAR CONSUMO DE UM PRODUTO
    // ==========================================

    private void adicionarConsumo(
            Map<Long, ConsumoEstoque> consumo,
            Produto produto,
            int quantidade) {

        Long produtoId = produto.getId();

        ConsumoEstoque existente = consumo.get(produtoId);

        if (existente == null) {

            consumo.put(
                    produtoId,
                    new ConsumoEstoque(produto, quantidade));

        } else {

            existente.quantidade += quantidade;
        }
    }


    // ==========================================
    // VALIDAR ESTOQUE TOTAL
    // ==========================================

    private void validarEstoqueTotal(
            Map<Long, ConsumoEstoque> consumo) {

        for (ConsumoEstoque item : consumo.values()) {

            if (item.produto.getQuantidadeEstoque()
                    < item.quantidade) {

                throw new IllegalArgumentException(
                        "Estoque insuficiente para o produto: "
                                + item.produto.getNome());
            }
        }
    }


    // ==========================================
    // BAIXAR ESTOQUE
    // ==========================================

    private void baixarEstoque(
            Map<Long, ConsumoEstoque> consumo) {

        for (ConsumoEstoque item : consumo.values()) {

            Produto produto = item.produto;

            produto.setQuantidadeEstoque(
                    produto.getQuantidadeEstoque()
                            - item.quantidade);

            produtoRepository.save(produto);
        }
    }


    // ==========================================
    // CALCULAR VALOR DOS ITENS
    // ==========================================

    private BigDecimal calcularValorItens(
            List<ItemCarrinho> itensCarrinho) {

        BigDecimal valorItens = BigDecimal.ZERO;

        for (ItemCarrinho item : itensCarrinho) {

            BigDecimal subtotal = item.getPrecoUnitario()
                    .multiply(BigDecimal.valueOf(
                            item.getQuantidade()));

            valorItens = valorItens.add(subtotal);
        }

        return valorItens.setScale(2, RoundingMode.HALF_UP);
    }


    // ==========================================
    // BUSCAR E VALIDAR CUPOM
    // ==========================================

    private Cupom buscarCupomValido(
            String codigoCupom,
            BigDecimal valorItens) {

        if (codigoCupom == null || codigoCupom.isBlank()) {
            return null;
        }

        Cupom cupom = cupomRepository.findByCodigo(codigoCupom)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cupom não encontrado."));

        if (!Boolean.TRUE.equals(cupom.getAtivo()) ||
                cupom.getValidade().isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Cupom expirado ou inativo.");
        }

        if (valorItens.compareTo(cupom.getValorMinimo()) < 0) {

            throw new IllegalArgumentException(
                    "Valor mínimo para utilizar o cupom não atingido.");
        }

        return cupom;
    }


    // ==========================================
    // CALCULAR DESCONTO
    // ==========================================

    private BigDecimal calcularDesconto(
            Cupom cupom,
            BigDecimal valorItens) {

        if (cupom == null) {
            return BigDecimal.ZERO;
        }

        return valorItens
                .multiply(cupom.getPercentualDesconto())
                .divide(new BigDecimal("100"),
                        2,
                        RoundingMode.HALF_UP);
    }


    // ==========================================
    // REGISTRAR ITEM DO PEDIDO
    // ==========================================

    private void registrarItemPedido(
            Pedido pedido,
            ItemCarrinho itemCarrinho) {

        ItemPedido itemPedido = new ItemPedido();

        itemPedido.setPedido(pedido);
        itemPedido.setQuantidade(itemCarrinho.getQuantidade());
        itemPedido.setPrecoUnitario(itemCarrinho.getPrecoUnitario());

        itemPedido.setSubtotal(
                itemCarrinho.getPrecoUnitario()
                        .multiply(BigDecimal.valueOf(
                                itemCarrinho.getQuantidade()))
                        .setScale(2, RoundingMode.HALF_UP));

        itemPedido.setProduto(itemCarrinho.getProduto());
        itemPedido.setKit(itemCarrinho.getKit());

        itemPedido = itemPedidoRepository.save(itemPedido);

        // Se for produto individual, não precisa de snapshot de kit
        if (itemCarrinho.getProduto() != null) {
            return;
        }

        // Se for kit, registrar seus componentes
        List<ItemKit> componentes =
                itemKitRepository.findByKitId(
                        itemCarrinho.getKit().getId());

        for (ItemKit componente : componentes) {

            ItemPedidoComponente registro =
                    new ItemPedidoComponente();

            registro.setItemPedido(itemPedido);
            registro.setProduto(componente.getProduto());

            registro.setQuantidadePorKit(
                    componente.getQuantidade());

            registro.setQuantidadeTotal(
                    componente.getQuantidade()
                            * itemCarrinho.getQuantidade());

            // Preserva o preço do produto no momento da compra
            registro.setPrecoUnitarioSnapshot(
                    componente.getProduto().getPreco());

            componenteRepository.save(registro);
        }
    }


    // ==========================================
    // VALIDAR PRODUTO
    // ==========================================

    private void validarProdutoAtivo(Produto produto) {

        if (!Boolean.TRUE.equals(produto.getAtivo())) {
            throw new IllegalArgumentException(
                    "Produto indisponível: " + produto.getNome());
        }
    }


    // ==========================================
    // VALIDAR KIT
    // ==========================================

    private void validarKitAtivo(Kit kit) {

        if (kit == null ||
                !Boolean.TRUE.equals(kit.getAtivo())) {

            throw new IllegalArgumentException(
                    "Kit indisponível.");
        }
    }


    // ==========================================
    // VALIDAR DISTÂNCIA
    // ==========================================

    private void validarDistancia(BigDecimal distanciaKm) {

        if (distanciaKm == null ||
                distanciaKm.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Informe uma distância válida.");
        }
    }


    // ==========================================
    // CRIAR ENTREGA
    // ==========================================

    private void criarEntrega(
            Pedido pedido,
            BigDecimal distanciaKm) {

        ConfiguracaoEntrega configuracao =
                configuracaoEntregaRepository.findByAtivoTrue()
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Configuração de entrega não encontrada."));

        Entrega entrega = new Entrega();

        entrega.setPedido(pedido);
        entrega.setStatus(StatusEntrega.EM_PREPARACAO);
        entrega.setDistanciaKm(distanciaKm);

        entrega.setPrazoEstimado(
                LocalDateTime.now().plusMinutes(
                        configuracao.getTempoMedioPreparo()));

        entregaRepository.save(entrega);
    }


    // ==========================================
    // GERAR NÚMERO DO PEDIDO
    // ==========================================

    private Integer gerarNumeroPedido() {

        Integer numero;

        do {
            numero = ThreadLocalRandom.current()
                    .nextInt(100000, 1000000);

        } while (
                pedidoRepository.findByNumeroPedido(numero).isPresent()
        );

        return numero;
    }


    // ==========================================
    // BUSCAR PEDIDO POR ID
    // ==========================================

    @Transactional(readOnly = true)
    public Pedido buscarPorId(Long id) {

        return pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Pedido não encontrado."));
    }


    // ==========================================
    // LISTAR PEDIDOS DO CLIENTE
    // ==========================================

    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(Long clienteId) {

        return pedidoRepository.findByClienteId(clienteId);
    }


    // ==========================================
    // LISTAR PEDIDOS POR STATUS
    // ==========================================

    @Transactional(readOnly = true)
    public List<Pedido> listarPorStatus(
            Long clienteId,
            StatusPedido status) {

        return pedidoRepository.findByClienteIdAndStatus(
                clienteId,
                status);
    }


    // ==========================================
    // CONFIRMAR PEDIDO
    // ==========================================

    @Transactional
    public Pedido confirmarPedido(Long id) {

        Pedido pedido = buscarPorId(id);

        if (pedido.getStatus() != StatusPedido.AGUARDANDO) {

            throw new IllegalArgumentException(
                    "Somente pedidos aguardando podem ser confirmados.");
        }

        pedido.setStatus(StatusPedido.CONFIRMADO);

        return pedidoRepository.save(pedido);
    }


    // ==========================================
    // CANCELAR PEDIDO E DEVOLVER ESTOQUE
    // ==========================================

    @Transactional
    public Pedido cancelarPedido(Long id) {

        Pedido pedido = buscarPorId(id);

        if (pedido.getStatus() == StatusPedido.CANCELADO ||
                pedido.getStatus() == StatusPedido.ENTREGUE) {

            throw new IllegalArgumentException(
                    "Não é possível cancelar este pedido.");
        }

        List<ItemPedido> itens =
                itemPedidoRepository.findByPedidoId(id);

        Map<Long, ConsumoEstoque> devolucao =
                new LinkedHashMap<>();

        // Identificar todos os produtos consumidos no pedido
        for (ItemPedido item : itens) {

            if (item.getProduto() != null) {

                adicionarConsumo(
                        devolucao,
                        item.getProduto(),
                        item.getQuantidade());

            } else if (item.getKit() != null) {

                List<ItemPedidoComponente> componentes =
                        componenteRepository.findByItemPedidoId(
                                item.getId());

                if (componentes.isEmpty()) {

                    throw new IllegalStateException(
                            "Não foi possível recuperar os componentes "
                                    + "do kit do pedido " + id);
                }

                for (ItemPedidoComponente componente : componentes) {

                    adicionarConsumo(
                            devolucao,
                            componente.getProduto(),
                            componente.getQuantidadeTotal());
                }
            }
        }

        // Devolver estoque de cada produto
        for (ConsumoEstoque item : devolucao.values()) {

            Produto produto = item.produto;

            produto.setQuantidadeEstoque(
                    produto.getQuantidadeEstoque()
                            + item.quantidade);

            produtoRepository.save(produto);
        }

        // Atualizar status do pedido
        pedido.setStatus(StatusPedido.CANCELADO);

        // Atualizar status da entrega
        Entrega entrega = entregaRepository.findByPedidoId(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Entrega do pedido não encontrada."));

        entrega.setStatus(StatusEntrega.CANCELADA);

        entregaRepository.save(entrega);

        return pedidoRepository.save(pedido);
    }


    // ==========================================
    // CLASSE AUXILIAR PARA CONTROLE DE ESTOQUE
    // ==========================================

    private static class ConsumoEstoque {

        private final Produto produto;
        private int quantidade;

        private ConsumoEstoque(
                Produto produto,
                int quantidade) {

            this.produto = produto;
            this.quantidade = quantidade;
        }
    }
}
