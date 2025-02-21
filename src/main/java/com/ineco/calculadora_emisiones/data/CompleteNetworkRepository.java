package com.ineco.calculadora_emisiones.data;

import com.ineco.calculadora_emisiones.model.pojo.CompleteNetwork;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CompleteNetworkRepository {

    private final CompleteNetworkJpaRepository repository;

    public void deleteAll(){
        repository.deleteAll();
    }

    public List<CompleteNetwork> getCompleteNetwork() {
        return repository.findAll();
    }

    public CompleteNetwork save(CompleteNetwork demanda) {
        return repository.save(demanda);
    }
}
