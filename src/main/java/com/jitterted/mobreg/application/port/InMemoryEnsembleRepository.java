package com.jitterted.mobreg.application.port;

import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryEnsembleRepository implements EnsembleRepository {
    private final Map<EnsembleId, Ensemble> ensembles = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);
    private int saveCount = 0;

    @Override
    public Ensemble save(Ensemble ensemble) {
        if (ensemble.getId() == null) {
            ensemble.setId(EnsembleId.of(sequence.getAndIncrement()));
        }
        ensembles.put(ensemble.getId(), ensemble);
        saveCount++;
        return ensemble;
    }

    @Override
    public List<Ensemble> findAll() {
        return List.copyOf(ensembles.values());
    }

    @Override
    public Optional<Ensemble> findById(EnsembleId ensembleId) {
        return Optional.ofNullable(ensembles.get(ensembleId));
    }

    public int saveCount() {
        return saveCount;
    }

    public void resetSaveCount() {
        saveCount = 0;
    }
}
