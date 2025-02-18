package com.ineco.calculadora_emisiones.service;

import com.ineco.calculadora_emisiones.model.request.CreateGenericCaseRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class CalculosEmisionesServiceImpl implements CalculosEmisionesService {

    private static final double FACTOR_EMISIONES_AEREAS_OP = 0.11415;
    private static final double FACTOR_EMISIONES_AEREAS_MAN = 0.91;

    @Getter
    public enum TipoTerreno {
        LLANO("Llano", 4000),
        ACCIDENTADO("Accidentado", 24000),
        MEDIO("Medio", 8000);

        private final String nombre;
        private final double emisiones;

        TipoTerreno(String nombre, double emisiones) {
            this.nombre = nombre;
            this.emisiones = emisiones;
        }

        // Método para obtener el enum desde el string
        public static TipoTerreno fromString(String nombre) {
            for (TipoTerreno t : TipoTerreno.values()) {
                // Use getNombre() instead of t.nombre directly
                if (t.getNombre().equalsIgnoreCase(nombre)) {
                    return t;
                }
            }
            return TipoTerreno.MEDIO; // Valor por defecto si no se encuentra
        }
    }

    @Getter
    public enum TamanoAeropuerto {
        GRANDE("Grande", 1809900),
        PEQUENO("Pequeño", 90350),
        MEDIANO("Mediano", 492100);

        private final String nombre;
        private final double emisiones;

        TamanoAeropuerto(String nombre, double emisiones) {
            this.nombre = nombre;
            this.emisiones = emisiones;
        }

        // Método para obtener el enum desde el string
        public static TamanoAeropuerto fromString(String nombre) {
            for (TamanoAeropuerto t : TamanoAeropuerto.values()) {
                if (t.getNombre().equalsIgnoreCase(nombre)) {
                    return t;
                }
            }
            return TamanoAeropuerto.MEDIANO; // Valor por defecto si no se encuentra
        }

    }

    /**
     * Cálculo de la emisión de construcción ferroviaria.
     *
     * @param request      Valores de entrada del front.
     * @return Valor de la emisión (t CO₂ eq)
     */
    public double emisionesConstruccionFerroviarias(CreateGenericCaseRequest request) {
        // Usar el método fromString para obtener el valor del enum correspondiente al tipo de terreno
        TipoTerreno tipoTerreno = TipoTerreno.fromString(request.getTipoTerreno());

        return request.getDistanciaFerroviaria() * tipoTerreno.getEmisiones()*request.getPorcentajeFerroviario()/100;
    }

    /**
     * Cálculo de la emisión de construcción aérea.
     *
     * @param request      Valores de entrada del front.
     * @return Valor de la emisión (t CO₂ eq)
     */
    public double emisionesConstruccionAereas(CreateGenericCaseRequest request) {
        // Usar el método fromString para obtener el valor del enum correspondiente al tamaño del aeropuerto
        TamanoAeropuerto tamanoAeropuertoA = TamanoAeropuerto.fromString(request.getTipoAeropuertoA());
        TamanoAeropuerto tamanoAeropuertoB = TamanoAeropuerto.fromString(request.getTipoAeropuertoB());

        return tamanoAeropuertoA.getEmisiones() * request.getPorcentajeAeropuertoA()/100 +
                tamanoAeropuertoB.getEmisiones() * request.getPorcentajeAeropuertoB()/100;
    }

    /**
     * Calcula la proyección de la demanda durante 50 años.
     *
     * @param request      Valores de entrada del front.
     * @return Lista con los diferentes valores de la demanda para cada año.
     */
    public Map<String, List<Double>> calcularProyeccion(CreateGenericCaseRequest request) {
        List<Double> proyeccion = new ArrayList<>();
        List<Double> demandaAerea = new ArrayList<>();
        List<Double> demandaFerroviaria = new ArrayList<>();
        double demandaActual = request.getDemandaInicial();

        for (int i = 1; i <= 50; i++) {
            // Agregar la demanda actual a la proyección
            proyeccion.add(demandaActual);

            // Calcular la demanda aérea y ferroviaria
            double porcentajeAereo = request.getPorcentajeAereo() / 100.0; // Convertir a decimal
            double porcentajeFerroviario = 1 - porcentajeAereo; // El resto es ferroviario

            demandaAerea.add(demandaActual * porcentajeAereo);
            demandaFerroviaria.add(demandaActual * porcentajeFerroviario);

            // Calcular la demanda para el próximo año
            demandaActual *= (1 + request.getCrecimiento()/100);
        }

        // Devolver los resultados en un mapa
        Map<String, List<Double>> resultados = new HashMap<>();
        resultados.put("proyeccionDemanda", proyeccion);
        resultados.put("demandaAerea", demandaAerea);
        resultados.put("demandaFerroviaria", demandaFerroviaria);

        return resultados;
    }

    /**
     * Cálculo de las emisiones de operación por años.
     *
     * @param request      Valores de entrada del front.
     * @return Lista con las emisiones de operación por año.
     */
    public List<Double> emisionesOperacionAerea(CreateGenericCaseRequest request) {
        // Obtener la lista de demanda aérea
        Map<String, List<Double>> proyeccion = calcularProyeccion(request);
        List<Double> demandaAerea = proyeccion.get("demandaAerea");

        // Lista para almacenar las emisiones de operación aérea
        List<Double> emisionesOperacion = new ArrayList<>();

        // Recorrer la lista de demanda aérea y calcular las emisiones
        for (Double demanda : demandaAerea) {
            double emisionAnual = demanda * request.getDistanciaAerea() * FACTOR_EMISIONES_AEREAS_OP/1000;
            emisionesOperacion.add(emisionAnual);
        }

        return emisionesOperacion;
    }

    /**
     * Cálculo de las emisiones de operación como si la demanda fuese 100% aérea por años.
     *
     * @param request      Valores de entrada del front.
     * @return Lista con las emisiones de operación por año.
     */
    public List<Double> emisionesOperacionTodoAereo(CreateGenericCaseRequest request) {
        // Obtener la lista de demanda total
        Map<String, List<Double>> proyeccion = calcularProyeccion(request);
        List<Double> demandaTotal = proyeccion.get("proyeccionDemanda");

        // Lista para almacenar las emisiones de operación aérea
        List<Double> emisionesOperacion = new ArrayList<>();

        // Recorrer la lista de demanda total y calcular las emisiones
        for (Double demanda : demandaTotal) {
            double emisionAnual = demanda * request.getDistanciaAerea() * FACTOR_EMISIONES_AEREAS_OP/1000;
            emisionesOperacion.add(emisionAnual);
        }

        return emisionesOperacion;
    }

    /**
     * Cálculo de las emisiones de mantenimiento aéreo por años.
     *
     * @param request      Valores de entrada del front.
     * @return Lista con las emisiones de mantenimiento aéreo por año.
     */
    public List<Double> emisionesMantenimientoAereo(CreateGenericCaseRequest request) {
        // Obtener la lista de demanda aérea
        Map<String, List<Double>> proyeccion = calcularProyeccion(request);
        List<Double> demandaAerea = proyeccion.get("demandaAerea");

        // Lista para almacenar las emisiones de operación aérea
        List<Double> emisionesMantenimiento = new ArrayList<>();

        // Recorrer la lista de demanda aérea y calcular las emisiones
        for (Double demanda : demandaAerea) {
            double emisionAnual = demanda * FACTOR_EMISIONES_AEREAS_MAN/1000;
            emisionesMantenimiento.add(emisionAnual);
        }

        return emisionesMantenimiento;
    }

    /**
     * Cálculo de las emisiones de mantenimiento aéreo, como si la demanda fuese 100% aérea, por años.
     *
     * @param request      Valores de entrada del front.
     * @return Lista con las emisiones de mantenimiento aéreo por año.
     */
    public List<Double> emisionesMantenimientoTodoAereo(CreateGenericCaseRequest request) {
        // Obtener la lista de demanda total
        Map<String, List<Double>> proyeccion = calcularProyeccion(request);
        List<Double> demandaTotal = proyeccion.get("proyeccionDemanda");

        // Lista para almacenar las emisiones de operación aérea
        List<Double> emisionesMantenimiento = new ArrayList<>();

        // Recorrer la lista de demanda aérea y calcular las emisiones
        for (Double demanda : demandaTotal) {
            double emisionAnual = demanda * FACTOR_EMISIONES_AEREAS_MAN/1000;
            emisionesMantenimiento.add(emisionAnual);
        }

        return emisionesMantenimiento;
    }

    /**
     * Cálculo de la emisión de mantenimiento ferroviaria.
     *
     * @param request      Valores de entrada del front.
     * @return Valor de la emisión de mantenimiento ferroviaria(t CO₂ eq)
     */
    public double emisionMantenimientoFerroviaria(CreateGenericCaseRequest request) {

        return request.getDistanciaFerroviaria() * 10 * request.getPorcentajeFerroviario()/100;
    }

    /**
     * Cálculo del ciclo de vida del AVE.
     *
     * @param request Valores de entrada del front.
     * @return Lista con las emisiones del ciclo de vida del AVE (55 valores).
     */
    public List<Double> cicloVidaAVE(CreateGenericCaseRequest request) {
        List<Double> cicloVida = new ArrayList<>();

        // Calcular las emisiones de construcción y dividirlas en 5 años
        double emisionesConstruccion = emisionesConstruccionFerroviarias(request);
        double emisionesConstruccionAnuales =  emisionesConstruccion / 5;

        // Agregar los primeros 5 valores (construcción)
        for (int i = 0; i < 5; i++) {
            cicloVida.add(emisionesConstruccionAnuales);
        }

        // Calcular las emisiones de mantenimiento
        double emisionesMantenimiento =  emisionMantenimientoFerroviaria(request);

        // Agregar los siguientes 50 valores (mantenimiento)
        for (int i = 0; i < 50; i++) {
            cicloVida.add(emisionesMantenimiento);
        }

        return cicloVida;
    }

    /**
     * Cálculo del ciclo de vida del transporte aéreo.
     *
     * @param request Valores de entrada del front.
     * @return Lista con las emisiones del ciclo de vida del transporte aéreo (55 valores).
     */
    public List<Double> cicloVidaAereo(CreateGenericCaseRequest request) {
        List<Double> cicloVidaAereo = new ArrayList<>();

        // Calcular las emisiones de construcción y dividirlas en 5 años
        double emisionesConstruccion = emisionesConstruccionAereas(request);
        double emisionesConstruccionAnuales = emisionesConstruccion / 5;

        // Agregar los primeros 5 valores (construcción)
        for (int i = 0; i < 5; i++) {
            cicloVidaAereo.add(emisionesConstruccionAnuales);
        }

        // Obtener las listas de emisiones de operación y mantenimiento
        List<Double> emisionesOperacion = emisionesOperacionAerea(request);
        List<Double> emisionesMantenimiento = emisionesMantenimientoAereo(request);

        // Sumar los valores correspondientes de ambas listas y agregarlos a la lista del ciclo de vida
        for (int i = 0; i < 50; i++) {
            double sumaEmisiones = emisionesOperacion.get(i) + emisionesMantenimiento.get(i);
            cicloVidaAereo.add(sumaEmisiones);
        }

        return cicloVidaAereo;
    }

    /**
     * Cálculo del ciclo de vida del transporte aéreo, como si la demanda fuese 100% aérea.
     *
     * @param request Valores de entrada del front.
     * @return Lista con las emisiones del ciclo de vida del transporte aéreo (55 valores).
     */
    public List<Double> cicloVidaTodoAereo(CreateGenericCaseRequest request) {
        List<Double> cicloVida = new ArrayList<>();

        // Calcular las emisiones de construcción y dividirlas en 5 años
        double emisionesConstruccion = emisionesConstruccionAereas(request);
        double emisionesConstruccionAnuales = emisionesConstruccion / 5;

        // Agregar los primeros 5 valores (construcción)
        for (int i = 0; i < 5; i++) {
            cicloVida.add(emisionesConstruccionAnuales);
        }

        // Obtener las listas de emisiones de operación y mantenimiento
        List<Double> emisionesOperacion = emisionesOperacionTodoAereo(request);
        List<Double> emisionesMantenimiento = emisionesMantenimientoTodoAereo(request);

        // Sumar los valores correspondientes de ambas listas y agregarlos a la lista del ciclo de vida
        for (int i = 0; i < 50; i++) {
            double sumaEmisiones = emisionesOperacion.get(i) + emisionesMantenimiento.get(i);
            cicloVida.add(sumaEmisiones);
        }

        return cicloVida;
    }

    /**
     * Cálculo de la suma del ciclo de vida ferroviario acumulado + aéreo acumulado.
     *
     * @param cicloVidaAVEAcumulado Lista de emisiones acumuladas del ciclo de vida del AVE.
     * @param cicloVidaAereoAcumulado Lista de emisiones acumuladas del ciclo de vida aéreo.
     * @return Lista con las emisiones acumuladas del transporte total (55 valores).
     */
    private List<Integer> sumaFerroviarioAereoAcumulado(List<Integer> cicloVidaAVEAcumulado, List<Integer> cicloVidaAereoAcumulado) {

        return IntStream.range(0, 55)
                .mapToObj(i -> cicloVidaAVEAcumulado.get(i) + cicloVidaAereoAcumulado.get(i))
                .collect(Collectors.toList());
    }

    /**
     * Calcula la suma acumulada de una lista de emisiones.
     *
     * @param emisiones Lista de emisiones.
     * @return Lista con las emisiones acumuladas.
     */
    private List<Integer> calcularCicloVidaAcumulado(List<Double> emisiones) {
        List<Integer> acumulado = new ArrayList<>();
        double sumaAcumulada = 0;

        for (Double emision : emisiones) {
            sumaAcumulada += emision;
            acumulado.add((int) Math.round(sumaAcumulada));
        }

        return acumulado;
    }

    /**
     * Método unificado para obtener todos los resultados necesarios para la representación deñ gráfico GEI sistema en el front.
     *
     * @param request Valores de entrada del front.
     * @return Mapa con los resultados de todos los métodos.
     */
    @Override
    public Map<String, List<Integer>> obtenerGraficoGEI(CreateGenericCaseRequest request) {
        Map<String, List<Integer>> resultados = new HashMap<>();

        // Calcular las listas comunes una sola vez
        List<Double> cicloVidaAVE = cicloVidaAVE(request);
        List<Double> cicloVidaAereo = cicloVidaAereo(request);
        List<Double> cicloVidaTodoAereo = cicloVidaTodoAereo(request);

        // Calcular las listas acumuladas
        List<Integer> cicloVidaAVEAcumulado = calcularCicloVidaAcumulado(cicloVidaAVE);
        List<Integer> cicloVidaAereoAcumulado = calcularCicloVidaAcumulado(cicloVidaAereo);
        List<Integer> cicloVidaTodoAereoAcumulado = calcularCicloVidaAcumulado(cicloVidaTodoAereo);

        Map<String, List<Double>> proyeccion = calcularProyeccion(request);
        List<Double> demandaAerea = proyeccion.get("demandaAerea");
        List<Double> demandaTren = proyeccion.get("demandaFerroviaria");

        // Calcular la suma acumulada de cicloVidaAVEAcumulado + cicloVidaAereoAcumulado
        List<Integer> sumaFerroviarioAereoAcumulado = sumaFerroviarioAereoAcumulado(cicloVidaAVEAcumulado, cicloVidaAereoAcumulado);

        // Agregar los resultados al mapa
        resultados.put("sumaFerroviarioAereoAcumulado", sumaFerroviarioAereoAcumulado);
        resultados.put("cicloVidaAVEAcumulado", cicloVidaAVEAcumulado);
        resultados.put("cicloVidaAereoAcumulado", cicloVidaAereoAcumulado);
        resultados.put("cicloVidaTodoAereoAcumulado", cicloVidaTodoAereoAcumulado);
        resultados.put("demandaTrenAcumulado", calcularCicloVidaAcumulado(demandaTren));
        resultados.put("demandaAvionAcumulado", calcularCicloVidaAcumulado(demandaAerea));

        return resultados;
    }
}