package com.ineco.calculadora_emisiones.model.pojo;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "complete_networks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CompleteNetwork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "cicloVidaAVEAcumulado")
    private Integer cicloVidaAVEAcumulado;

    @Column(name = "cicloVidaAereoAcumulado")
    private Integer cicloVidaAereoAcumulado;

    @Column(name = "demandaAVLDAcumulada")
    private Integer demandaAVLDAcumulada;

    @Column(name = "demandaAereaAcumulada")
    private Integer demandaAereaAcumulada;
}
