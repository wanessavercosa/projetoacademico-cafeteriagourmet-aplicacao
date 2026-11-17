package com.chouchougourmet.projeto.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "faixa_taxa_entrega")
public class FaixaTaxaEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "distancia_minima", nullable = false,
            precision = 6, scale = 2)
    private BigDecimal distanciaMinima;

    @Column(name = "distancia_maxima", nullable = false,
            precision = 6, scale = 2)
    private BigDecimal distanciaMaxima;

    @Column(name = "valor_taxa", nullable = false,
            precision = 10, scale = 2)
    private BigDecimal valorTaxa;

    @Column(nullable = false)
    private Boolean ativo = true;
}

