package com.ineco.calculadora_emisiones.model.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateGenericCaseRequest {

    private Double distanciaOD;

    private Double demandaInicial;

    private Double  crecimiento;

    private Double porcentajeAereo;

    private Double distanciaFerroviaria;

    private String tipoTerreno;

    private Double porcentajeFerroviario;

    private Double distanciaAerea;

    private String tipoAeropuertoA;

    private Double porcentajeAeropuertoA;

    private String tipoAeropuertoB;

    private Double porcentajeAeropuertoB;
}
