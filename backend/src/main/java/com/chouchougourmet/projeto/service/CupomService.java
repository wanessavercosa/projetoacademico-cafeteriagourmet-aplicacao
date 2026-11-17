package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.dto.ValidarCupomRequest;
import com.chouchougourmet.projeto.model.entity.Cupom;
import com.chouchougourmet.projeto.repository.CupomRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CupomService {

    private final CupomRepository cupomRepository;

    // Buscar cupom pelo código
    @Transactional(readOnly = true)
    public Cupom buscarPorCodigo(String codigo) {

        return cupomRepository.findByCodigo(codigo)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Cupom não encontrado."
                        )
                );
    }

    // Validar cupom
    @Transactional(readOnly = true)
    public Cupom validar(ValidarCupomRequest dados) {

        Cupom cupom = buscarPorCodigo(dados.codigo());

        if (!cupom.getAtivo()) {
            throw new IllegalArgumentException(
                    "Este cupom está inativo."
            );
        }

        // A validade é uma data final, inclusive
        if (LocalDate.now().isAfter(cupom.getValidade())) {
            throw new IllegalArgumentException(
                    "Este cupom está vencido."
            );
        }

        if (dados.valorPedido().compareTo(
                cupom.getValorMinimo()) < 0) {

            throw new IllegalArgumentException(
                    "O pedido não atingiu o valor mínimo para utilizar o cupom."
            );
        }

        return cupom;
    }

    // Calcular desconto percentual
    @Transactional(readOnly = true)
    public BigDecimal calcularDesconto(
            ValidarCupomRequest dados) {

        Cupom cupom = validar(dados);

        BigDecimal desconto = dados.valorPedido()
                .multiply(cupom.getPercentualDesconto())
                .divide(
                        new BigDecimal("100"),
                        2,
                        RoundingMode.HALF_UP
                );

        return desconto.setScale(2, RoundingMode.HALF_UP);
    }
}
