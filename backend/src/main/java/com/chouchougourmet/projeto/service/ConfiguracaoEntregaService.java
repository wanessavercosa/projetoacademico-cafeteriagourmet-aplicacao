package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.model.entity.ConfiguracaoEntrega;
import com.chouchougourmet.projeto.repository.ConfiguracaoEntregaRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfiguracaoEntregaService {

    private final ConfiguracaoEntregaRepository configuracaoRepository;

    // Listar configurações
    @Transactional(readOnly = true)
    public List<ConfiguracaoEntrega> listarTodas() {
        return configuracaoRepository.findAll();
    }

    // Buscar configuração ativa
    @Transactional(readOnly = true)
    public ConfiguracaoEntrega buscarAtiva() {

        return configuracaoRepository.findByAtivoTrue()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Nenhuma configuração de entrega ativa."
                        )
                );
    }

    // Cadastrar configuração
    @Transactional
    public ConfiguracaoEntrega cadastrar(
            ConfiguracaoEntrega configuracao) {

        validarTempo(configuracao.getTempoMedioPreparo());

        if (Boolean.TRUE.equals(configuracao.getAtivo()) &&
                configuracaoRepository.findByAtivoTrue().isPresent()) {

            throw new IllegalArgumentException(
                    "Já existe uma configuração de entrega ativa."
            );
        }

        return configuracaoRepository.save(configuracao);
    }

    // Atualizar configuração
    @Transactional
    public ConfiguracaoEntrega atualizar(
            Long id,
            ConfiguracaoEntrega dados) {

        validarTempo(dados.getTempoMedioPreparo());

        ConfiguracaoEntrega configuracao = buscarPorId(id);

        if (Boolean.TRUE.equals(dados.getAtivo())) {

            boolean outraAtiva = configuracaoRepository.findByAtivoTrue()
                    .stream()
                    .anyMatch(item -> !item.getId().equals(id));

            if (outraAtiva) {
                throw new IllegalArgumentException(
                        "Já existe outra configuração de entrega ativa."
                );
            }
        }

        configuracao.setTempoMedioPreparo(
                dados.getTempoMedioPreparo()
        );

        configuracao.setAtivo(dados.getAtivo());

        return configuracaoRepository.save(configuracao);
    }

    // Buscar configuração pelo ID
    @Transactional(readOnly = true)
    public ConfiguracaoEntrega buscarPorId(Long id) {

        return configuracaoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Configuração de entrega não encontrada."
                        )
                );
    }

    // Inativar configuração
    @Transactional
    public void inativar(Long id) {

        ConfiguracaoEntrega configuracao = buscarPorId(id);

        configuracao.setAtivo(false);

        configuracaoRepository.save(configuracao);
    }

    // Validar tempo de preparo
    private void validarTempo(Integer tempo) {

        if (tempo == null || tempo < 0) {
            throw new IllegalArgumentException(
                    "O tempo médio de preparo não pode ser negativo."
            );
        }
    }
}
