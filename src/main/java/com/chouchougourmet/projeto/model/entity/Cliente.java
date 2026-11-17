package com.chouchougourmet.projeto.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cliente")
public class Cliente extends Usuario {

    @Column(name = "endereco_padrao_id")
    private Long enderecoPadraoId;
}

