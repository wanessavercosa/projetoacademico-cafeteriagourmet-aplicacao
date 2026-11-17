package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.dto.CadastroKitRequest;
import com.chouchougourmet.projeto.model.entity.ItemKit;
import com.chouchougourmet.projeto.model.entity.Kit;
import com.chouchougourmet.projeto.model.entity.Produto;
import com.chouchougourmet.projeto.repository.ItemKitRepository;
import com.chouchougourmet.projeto.repository.KitRepository;
import com.chouchougourmet.projeto.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KitService {

    private final KitRepository kitRepository;
    private final ItemKitRepository itemKitRepository;
    private final ProdutoRepository produtoRepository;

    // Listar kits ativos
    @Transactional(readOnly = true)
    public List<Kit> listarAtivos() {
        return kitRepository.findByAtivoTrue();
    }

    // Buscar kit pelo ID
    @Transactional(readOnly = true)
    public Kit buscarPorId(Long id) {
        return kitRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Kit não encontrado."
                        )
                );
    }

    // Cadastrar kit com seus componentes
    @Transactional
    public Kit cadastrar(CadastroKitRequest dados) {

        validarDados(dados);

        Kit kit = new Kit();

        kit.setNome(dados.nome());
        kit.setDescricao(dados.descricao());
        kit.setPreco(dados.preco());
        kit.setAtivo(true);

        Kit kitSalvo = kitRepository.save(kit);

        salvarComponentes(kitSalvo, dados.componentes());

        return kitSalvo;
    }

    // Atualizar kit e seus componentes
    @Transactional
    public Kit atualizar(Long id, CadastroKitRequest dados) {

        validarDados(dados);

        Kit kit = buscarPorId(id);

        kit.setNome(dados.nome());
        kit.setDescricao(dados.descricao());
        kit.setPreco(dados.preco());

        kitRepository.save(kit);

        // Remove os componentes antigos
        List<ItemKit> componentesAntigos =
                itemKitRepository.findByKitId(id);

        itemKitRepository.deleteAll(componentesAntigos);

        // Salva os novos componentes
        salvarComponentes(kit, dados.componentes());

        return kit;
    }

    // Inativar kit
    @Transactional
    public void inativar(Long id) {

        Kit kit = buscarPorId(id);

        kit.setAtivo(false);

        kitRepository.save(kit);
    }

    // Salvar componentes do kit
    private void salvarComponentes(
            Kit kit,
            List<CadastroKitRequest.ItemKitRequest> componentes) {

        for (CadastroKitRequest.ItemKitRequest componente : componentes) {

            Produto produto = produtoRepository.findById(
                    componente.produtoId()
            ).orElseThrow(() ->
                    new EntityNotFoundException(
                            "Produto não encontrado: "
                                    + componente.produtoId()
                    )
            );

            if (!produto.getAtivo()) {
                throw new IllegalArgumentException(
                        "Não é possível adicionar um produto inativo ao kit."
                );
            }

            ItemKit itemKit = new ItemKit();

            itemKit.setKit(kit);
            itemKit.setProduto(produto);
            itemKit.setQuantidade(componente.quantidade());

            itemKitRepository.save(itemKit);
        }
    }

    // Validar dados do kit
    private void validarDados(CadastroKitRequest dados) {

        if (dados.preco().signum() < 0) {
            throw new IllegalArgumentException(
                    "O preço do kit não pode ser negativo."
            );
        }

        if (dados.componentes() == null ||
                dados.componentes().isEmpty()) {
            throw new IllegalArgumentException(
                    "O kit precisa ter pelo menos um produto."
            );
        }

        // Impede repetir o mesmo produto no kit
        long produtosDistintos = dados.componentes()
                .stream()
                .map(CadastroKitRequest.ItemKitRequest::produtoId)
                .distinct()
                .count();

        if (produtosDistintos != dados.componentes().size()) {
            throw new IllegalArgumentException(
                    "Não repita o mesmo produto nos componentes do kit."
            );
        }
    }
}
