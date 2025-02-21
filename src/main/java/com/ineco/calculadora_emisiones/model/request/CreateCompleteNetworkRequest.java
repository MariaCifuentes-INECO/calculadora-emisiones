package com.ineco.calculadora_emisiones.model.request;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCompleteNetworkRequest {

    private Integer anio;

    private Integer cicloVidaAVEAcumulado;

    private Integer cicloVidaAereoAcumulado;

    private Integer demandaAVLDAcumulada;

    private Integer demandaAereaAcumulada;
}
