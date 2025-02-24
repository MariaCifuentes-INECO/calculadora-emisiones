package com.ineco.calculadora_emisiones.service;

import com.ineco.calculadora_emisiones.data.CompleteNetworkRepository;
import com.ineco.calculadora_emisiones.model.pojo.CompleteNetwork;
import com.ineco.calculadora_emisiones.model.request.CreateCompleteNetworkRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CompleteNetworksServiceImpl implements CompleteNetworksService {

    @Autowired
    private CompleteNetworkRepository repository;

    @Override
    public List<CompleteNetwork> createCompleteNetwork(List<CreateCompleteNetworkRequest> requests) {
        repository.deleteAll();

        List<CompleteNetwork> completeNetworks = new ArrayList<>();

        requests.forEach(r -> {
            CompleteNetwork completeNetwork = CompleteNetwork.builder()
                    .anio(r.getAnio())
                    .emisionesConstruccionAVE(r.getEmisionesConstruccionAVE())
                    .emisionesMantenimientoAVE(r.getEmisionesMantenimientoAVE())
                    .cicloVidaAVEAcumulado(r.getCicloVidaAVEAcumulado())
                    .emisionesConstruccionAereo(r.getEmisionesConstruccionAereo())
                    .emisionesOperacionAereo(r.getEmisionesOperacionAereo())
                    .emisionesMantenimientoAereo(r.getEmisionesMantenimientoAereo())
                    .cicloVidaAereoAcumulado(r.getCicloVidaAereoAcumulado())
                    .demandaAVLDAcumulada(r.getDemandaAVLDAcumulada())
                    .demandaAereaAcumulada(r.getDemandaAereaAcumulada())
                    .build();

            // Guardar el objeto en la base de datos
            completeNetwork = repository.save(completeNetwork);

            completeNetworks.add(completeNetwork);
        });

        return completeNetworks;
    }

    @Override
    public List<CompleteNetwork> getCompleteNetwork() {

        List<CompleteNetwork> completeNetwork = repository.getCompleteNetwork();
        return completeNetwork.isEmpty() ? null : completeNetwork;
    }
}