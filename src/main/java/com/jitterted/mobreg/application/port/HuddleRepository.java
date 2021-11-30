package com.jitterted.mobreg.application.port;

import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;

import java.util.List;
import java.util.Optional;

public interface HuddleRepository {
    Ensemble save(Ensemble ensemble);

    List<Ensemble> findAll();

    Optional<Ensemble> findById(EnsembleId ensembleId);
}
