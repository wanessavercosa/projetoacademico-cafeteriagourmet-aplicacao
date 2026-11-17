package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.model.entity.FaixaTaxaEntrega;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EntregaService {

    private final FaixaTaxaEntregaService faixaTaxaEntregaService;

    public BigDecimal calcularTaxaEntrega(BigDecimal distanciaKm) {

        FaixaTaxaEntrega faixa =
                faixaTaxaEntregaService.buscarFaixaPorDistancia(
                        distanciaKm
                );

        return faixa.getValorTaxa();
    }
}