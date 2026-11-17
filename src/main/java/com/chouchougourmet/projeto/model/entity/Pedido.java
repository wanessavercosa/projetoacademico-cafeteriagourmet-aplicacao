package com.chouchougourmet.projeto.model.entity;

import com.chouchougourmet.projeto.model.enums.StatusPedido;
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
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id")
    private Funcionario funcionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id", nullable = false)
    private Endereco endereco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cupom_id")
    private Cupom cupom;

    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusPedido status;

    @Column(name = "valor_itens", nullable = false,
            precision = 10, scale = 2)
    private BigDecimal valorItens;

    @Column(name = "valor_desconto", nullable = false,
            precision = 10, scale = 2)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(name = "taxa_entrega", nullable = false,
            precision = 10, scale = 2)
    private BigDecimal taxaEntrega = BigDecimal.ZERO;

    @Column(name = "valor_total", nullable = false,
            precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "numero_pedido", nullable = false, unique = true)
    private Integer numeroPedido;
}

