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

    private Integer emisionesConstruccionAVE;

    private Integer emisionesMantenimientoAVE;

    private Integer cicloVidaAVEAcumulado;

    private Integer emisionesConstruccionAereo;

    private Integer emisionesOperacionAereo;

    private Integer emisionesMantenimientoAereo;

    private Integer cicloVidaAereoAcumulado;

    private Integer demandaAVLDAcumulada;

    private Integer demandaAereaAcumulada;
}
