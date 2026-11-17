package com.chouchougourmet.projeto.model.entity;

import com.chouchougourmet.projeto.model.enums.StatusEntrega;
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
@Table(name = "entrega")
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @Column(name = "data_agendada")
    private LocalDateTime dataAgendada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusEntrega status;

    @Column(name = "distancia_km", nullable = false,
            precision = 6, scale = 2)
    private BigDecimal distanciaKm;

    @Column(name = "prazo_estimado")
    private LocalDateTime prazoEstimado;
}

