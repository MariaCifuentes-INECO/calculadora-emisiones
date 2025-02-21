package com.ineco.calculadora_emisiones.data;

import com.ineco.calculadora_emisiones.model.pojo.CompleteNetwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompleteNetworkJpaRepository extends JpaRepository<CompleteNetwork, Long>, JpaSpecificationExecutor<CompleteNetwork> {
}
