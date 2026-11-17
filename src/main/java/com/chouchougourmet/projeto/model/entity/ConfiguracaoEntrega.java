package com.chouchougourmet.projeto.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "configuracao_entrega")
public class ConfiguracaoEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tempo_medio_preparo", nullable = false)
    private Integer tempoMedioPreparo;

    @Column(nullable = false)
    private Boolean ativo = true;
}

