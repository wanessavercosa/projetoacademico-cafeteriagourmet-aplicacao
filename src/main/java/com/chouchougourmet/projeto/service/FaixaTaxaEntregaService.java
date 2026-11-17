package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.model.entity.FaixaTaxaEntrega;
import com.chouchougourmet.projeto.repository.FaixaTaxaEntregaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FaixaTaxaEntregaService {

    private final FaixaTaxaEntregaRepository faixaRepository;

    @Transactional(readOnly = true)
    public List<FaixaTaxaEntrega> listarAtivas() {

        return faixaRepository.findByAtivoTrue()
                .stream()
                .sorted(
                        Comparator.comparing(
                                FaixaTaxaEntrega::getDistanciaMinima
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public FaixaTaxaEntrega buscarPorId(Long id) {

        return faixaRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Faixa de entrega não encontrada."
                        )
                );
    }

    @Transactional
    public FaixaTaxaEntrega cadastrar(
            FaixaTaxaEntrega faixa) {

        validarFaixa(faixa);

        validarSobreposicao(
                faixa,
                null
        );

        faixa.setAtivo(true);

        return faixaRepository.save(faixa);
    }

    @Transactional
    public FaixaTaxaEntrega atualizar(
            Long id,
            FaixaTaxaEntrega dados) {

        validarFaixa(dados);

        FaixaTaxaEntrega faixa = buscarPorId(id);

        if (Boolean.TRUE.equals(dados.getAtivo())) {

            validarSobreposicao(
                    dados,
                    id
            );
        }

        faixa.setDistanciaMinima(
                dados.getDistanciaMinima()
        );

        faixa.setDistanciaMaxima(
                dados.getDistanciaMaxima()
        );

        faixa.setValorTaxa(
                dados.getValorTaxa()
        );

        faixa.setAtivo(
                dados.getAtivo()
        );

        return faixaRepository.save(faixa);
    }

    @Transactional(readOnly = true)
    public FaixaTaxaEntrega buscarFaixaPorDistancia(
            BigDecimal distancia) {

        if (distancia == null ||
                distancia.signum() < 0) {

            throw new IllegalArgumentException(
                    "Informe uma distância válida."
            );
        }

        return faixaRepository.findByAtivoTrue()
                .stream()
                .sorted(
                        Comparator.comparing(
                                FaixaTaxaEntrega::getDistanciaMinima
                        )
                )
                .filter(faixa ->
                        distancia.compareTo(
                                faixa.getDistanciaMinima()
                        ) >= 0
                                &&
                                distancia.compareTo(
                                        faixa.getDistanciaMaxima()
                                ) < 0
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Não existe cobertura de entrega para esta distância."
                        )
                );
    }

    @Transactional
    public void inativar(Long id) {

        FaixaTaxaEntrega faixa = buscarPorId(id);

        faixa.setAtivo(false);

        faixaRepository.save(faixa);
    }

    private void validarFaixa(
            FaixaTaxaEntrega faixa) {

        if (faixa.getDistanciaMinima() == null ||
                faixa.getDistanciaMaxima() == null ||
                faixa.getValorTaxa() == null) {

            throw new IllegalArgumentException(
                    "Preencha os limites de distância e o valor da taxa."
            );
        }

        if (faixa.getDistanciaMinima().signum() < 0) {

            throw new IllegalArgumentException(
                    "A distância mínima não pode ser negativa."
            );
        }

        if (faixa.getDistanciaMaxima()
                .compareTo(
                        faixa.getDistanciaMinima()
                ) <= 0) {

            throw new IllegalArgumentException(
                    "A distância máxima deve ser maior que a mínima."
            );
        }

        if (faixa.getValorTaxa().signum() < 0) {

            throw new IllegalArgumentException(
                    "O valor da taxa não pode ser negativo."
            );
        }
    }

    private void validarSobreposicao(
            FaixaTaxaEntrega novaFaixa,
            Long idIgnorado) {

        boolean existeSobreposicao =
                faixaRepository.findByAtivoTrue()
                        .stream()
                        .filter(faixa ->
                                idIgnorado == null ||
                                        !faixa.getId().equals(
                                                idIgnorado
                                        )
                        )
                        .anyMatch(faixa ->
                                novaFaixa.getDistanciaMinima()
                                        .compareTo(
                                                faixa.getDistanciaMaxima()
                                        ) < 0
                                        &&
                                        novaFaixa.getDistanciaMaxima()
                                                .compareTo(
                                                        faixa.getDistanciaMinima()
                                                ) > 0
                        );

        if (existeSobreposicao) {

            throw new IllegalArgumentException(
                    "Esta faixa se sobrepõe a outra faixa ativa."
            );
        }
    }
}