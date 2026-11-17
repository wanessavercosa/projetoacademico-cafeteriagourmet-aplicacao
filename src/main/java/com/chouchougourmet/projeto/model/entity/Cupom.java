package com.chouchougourmet.projeto.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cupom")
public class Cupom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(name = "percentual_desconto", nullable = false,
            precision = 5, scale = 2)
    private BigDecimal percentualDesconto;

    @Column(name = "valor_minimo", nullable = false,
            precision = 10, scale = 2)
    private BigDecimal valorMinimo;

    @Column(nullable = false)
    private LocalDate validade;

    @Column(nullable = false)
    private Boolean ativo = true;
}