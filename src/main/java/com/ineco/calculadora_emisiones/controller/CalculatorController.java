package com.ineco.calculadora_emisiones.controller;

import com.ineco.calculadora_emisiones.model.request.CreateGenericCaseRequest;
import com.ineco.calculadora_emisiones.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Calculator Controller", description = "Microservicio encargado de exponer operaciones CRUD sobre los datos de la calculadora.")
public class CalculatorController {

    private final CalculosEmisionesService calculosEmisionesService;

    /**
     * Obtiene los datos necesarios para representar el gráfico GEI sistema.
     *
     * @param request Datos de entrada para el cálculo.
     * @return Un mapa con las listas de datos necesarios para el gráfico.
     */
    @PostMapping("/graficoGEISistema")
    @Operation(
            operationId = "Obtener los datos necesarios para représentar el gráfico GEI sistema",
            description = "Operación de lectura",
            summary = "Se devuelve un objeto con todas las listas de datos necesarios para el gráfico GEI sistema.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, List<Integer>>> getGraficoGEISistema(
            @RequestBody CreateGenericCaseRequest request) {

        Map<String, List<Integer>> datos = calculosEmisionesService.obtenerGraficoGEI(request);

        return ResponseEntity.ok(datos);
    }

}
