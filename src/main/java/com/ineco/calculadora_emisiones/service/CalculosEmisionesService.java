package com.ineco.calculadora_emisiones.service;

import com.ineco.calculadora_emisiones.model.request.CreateGenericCaseRequest;

import java.util.List;
import java.util.Map;

public interface CalculosEmisionesService {

    Map<String, List<Integer>> obtenerGraficoGEI(CreateGenericCaseRequest request);
}
