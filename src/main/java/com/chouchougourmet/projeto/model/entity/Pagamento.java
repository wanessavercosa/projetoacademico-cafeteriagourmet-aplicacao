package com.chouchougourmet.projeto.model.entity;

import com.chouchougourmet.projeto.model.enums.FormaPagamento;
import com.chouchougourmet.projeto.model.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 30)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusPagamento status;

    @Column(name = "valor_total", nullable = false,
            precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "codigo_transacao", length = 80)
    private String codigoTransacao;
}

