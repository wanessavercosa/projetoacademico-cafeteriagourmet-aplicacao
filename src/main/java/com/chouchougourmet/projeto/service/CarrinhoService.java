package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.model.entity.*;
import com.chouchougourmet.projeto.repository.*;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final KitRepository kitRepository;

     @Transactional
    public Carrinho buscarOuCriar(Long clienteId) {

        return carrinhoRepository.findByClienteId(clienteId)
                .orElseGet(() -> {

                    Cliente cliente = clienteRepository.findById(clienteId)
                            .orElseThrow(() ->
                                    new EntityNotFoundException(
                                            "Cliente não encontrado."
                                    )
                            );

                    Carrinho carrinho = new Carrinho();
                    carrinho.setCliente(cliente);

                    return carrinhoRepository.save(carrinho);
                });
    }

    @Transactional
    public ItemCarrinho adicionarProduto(
            Long clienteId,
            Long produtoId,
            Integer quantidade) {

        validarQuantidade(quantidade);

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Produto não encontrado."
                        )
                );

        if (!produto.getAtivo()) {
            throw new IllegalArgumentException(
                    "Este produto não está disponível."
            );
        }

        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new IllegalArgumentException(
                    "Estoque insuficiente."
            );
        }

        Carrinho carrinho = buscarOuCriar(clienteId);

        List<ItemCarrinho> itens =
                itemCarrinhoRepository.findByCarrinhoId(
                        carrinho.getId()
                );

        for (ItemCarrinho item : itens) {

            if (item.getProduto() != null &&
                    item.getProduto().getId().equals(produtoId)) {

                item.setQuantidade(
                        item.getQuantidade() + quantidade
                );

                item.setPrecoUnitario(produto.getPreco());

                return itemCarrinhoRepository.save(item);
            }
        }

        ItemCarrinho novoItem = new ItemCarrinho();

        novoItem.setCarrinho(carrinho);
        novoItem.setProduto(produto);
        novoItem.setKit(null);
        novoItem.setQuantidade(quantidade);
        novoItem.setPrecoUnitario(produto.getPreco());

        return itemCarrinhoRepository.save(novoItem);
    }

    @Transactional
    public ItemCarrinho adicionarKit(
            Long clienteId,
            Long kitId,
            Integer quantidade) {

        validarQuantidade(quantidade);

        Kit kit = kitRepository.findById(kitId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Kit não encontrado."
                        )
                );

        if (!kit.getAtivo()) {
            throw new IllegalArgumentException(
                    "Este kit não está disponível."
            );
        }

        Carrinho carrinho = buscarOuCriar(clienteId);

        List<ItemCarrinho> itens =
                itemCarrinhoRepository.findByCarrinhoId(
                        carrinho.getId()
                );

        for (ItemCarrinho item : itens) {

            if (item.getKit() != null &&
                    item.getKit().getId().equals(kitId)) {

                item.setQuantidade(
                        item.getQuantidade() + quantidade
                );

                item.setPrecoUnitario(kit.getPreco());

                return itemCarrinhoRepository.save(item);
            }
        }

        ItemCarrinho novoItem = new ItemCarrinho();

        novoItem.setCarrinho(carrinho);
        novoItem.setProduto(null);
        novoItem.setKit(kit);
        novoItem.setQuantidade(quantidade);
        novoItem.setPrecoUnitario(kit.getPreco());

        return itemCarrinhoRepository.save(novoItem);
    }

    @Transactional(readOnly = true)
    public List<ItemCarrinho> listarItens(Long clienteId) {

        Carrinho carrinho = carrinhoRepository.findByClienteId(clienteId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Carrinho não encontrado."
                        )
                );

        return itemCarrinhoRepository.findByCarrinhoId(
                carrinho.getId()
        );
    }

    @Transactional
    public void removerItem(Long clienteId, Long itemId) {

        Carrinho carrinho = carrinhoRepository.findByClienteId(clienteId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Carrinho não encontrado."
                        )
                );

        ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Item não encontrado."
                        )
                );

        if (!item.getCarrinho().getId().equals(carrinho.getId())) {
            throw new IllegalArgumentException(
                    "Este item não pertence ao carrinho do cliente."
            );
        }

        itemCarrinhoRepository.delete(item);
    }

    private void validarQuantidade(Integer quantidade) {

        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }
    }
}
