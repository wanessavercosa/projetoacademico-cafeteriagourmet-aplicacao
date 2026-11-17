package com.chouchougourmet.projeto.service;

import com.chouchougourmet.projeto.dto.ValidarCupomRequest;
import com.chouchougourmet.projeto.model.entity.Cupom;
import com.chouchougourmet.projeto.repository.CupomRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CupomServiceTest {

    @Mock
    private CupomRepository cupomRepository;

    @InjectMocks
    private CupomService cupomService;

    @Test
    void deveCalcularDescontoCorretamente() {

        Cupom cupom = new Cupom();

        cupom.setCodigo("CHOUCHOU10");
        cupom.setAtivo(true);
        cupom.setValidade(LocalDate.now().plusDays(10));
        cupom.setPercentualDesconto(
                new BigDecimal("10")
        );
        cupom.setValorMinimo(
                new BigDecimal("50")
        );

        when(cupomRepository.findByCodigo("CHOUCHOU10"))
                .thenReturn(Optional.of(cupom));

        ValidarCupomRequest request =
                new ValidarCupomRequest(
                        "CHOUCHOU10",
                        new BigDecimal("100")
                );

        BigDecimal desconto =
                cupomService.calcularDesconto(request);

        assertEquals(
                new BigDecimal("10.00"),
                desconto
        );
    }

    @Test
    void deveLancarErroQuandoCupomEstiverVencido() {

        Cupom cupom = new Cupom();

        cupom.setCodigo("CHOUCHOU10");
        cupom.setAtivo(true);
        cupom.setValidade(
                LocalDate.now().minusDays(1)
        );

        cupom.setPercentualDesconto(
                new BigDecimal("10")
        );

        cupom.setValorMinimo(
                new BigDecimal("50")
        );

        when(cupomRepository.findByCodigo("CHOUCHOU10"))
                .thenReturn(Optional.of(cupom));

        ValidarCupomRequest request =
                new ValidarCupomRequest(
                        "CHOUCHOU10",
                        new BigDecimal("100")
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> cupomService.calcularDesconto(
                                request
                        )
                );

        assertEquals(
                "Este cupom está vencido.",
                exception.getMessage()
        );
    }


}