package com.ineco.calculadora_emisiones.controller;

import com.ineco.calculadora_emisiones.model.pojo.CompleteNetwork;
import com.ineco.calculadora_emisiones.model.request.CreateCompleteNetworkRequest;
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

    private final CompleteNetworksService completeNetworksService;

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

    /**
     * Crea la base de datos de la red completa de 2004 a 2023
     *
     * @param request Datos de entrada de la red completa a pasado.
     * @return Una lista con los datos de la red completa de 2004 a 2023.
     */
    @PostMapping("/redCompleta")
    @Operation(
            operationId = "Crea la base de datos de la red completa de 2004 a 2023",
            description = "Operación de escritura",
            summary = "Se devuelve una lista con los datos de la red completa de 2004 a 2023.")
    @ApiResponse(
            responseCode = "201",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompleteNetwork.class)))
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Datos introducidos incorrectos.")
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado la demanda con el identificador indicado.")
    public ResponseEntity<List<CompleteNetwork>> addCompleteNetwork(
            @RequestBody List<CreateCompleteNetworkRequest> request) {

        List<CompleteNetwork> datos = completeNetworksService.createCompleteNetwork(request);

        return ResponseEntity.ok(datos);
    }

    /**
     * Obtiene los datos necesarios para representar el análisis de 2004 a 2023.
     *
     * @return Una listas de datos necesarios para el gráfico.
     */
    @GetMapping("/redCompleta")
    @Operation(
            operationId = "Obtener los datos necesarios para représentar el análisis de 2004 a 2023",
            description = "Operación de lectura",
            summary = "Se devuelve una lista de datos necesarios para el gráfico.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompleteNetwork.class)))
    public ResponseEntity<List<CompleteNetwork>> getRedCompletaReal(
    ) {

        List<CompleteNetwork> datos = completeNetworksService.getCompleteNetwork();

        return ResponseEntity.ok(datos);
    }
}
