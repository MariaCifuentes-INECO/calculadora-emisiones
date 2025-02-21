package com.ineco.calculadora_emisiones.service;

import com.ineco.calculadora_emisiones.model.pojo.CompleteNetwork;
import com.ineco.calculadora_emisiones.model.request.CreateCompleteNetworkRequest;

import java.util.List;

public interface CompleteNetworksService {

    List<CompleteNetwork> createCompleteNetwork(List<CreateCompleteNetworkRequest> requests);

    public List<CompleteNetwork> getCompleteNetwork();
}
