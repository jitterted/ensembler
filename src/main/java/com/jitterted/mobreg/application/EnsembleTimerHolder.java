package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class EnsembleTimerHolder {
    private final EnsembleRepository ensembleRepository;
    private final SingletonHashMap<EnsembleId, EnsembleTimer> ensembleTimers = new SingletonHashMap<>();

    public EnsembleTimerHolder(EnsembleRepository ensembleRepository) {
        this.ensembleRepository = ensembleRepository;
    }

    @NotNull
    public EnsembleTimer timerFor(EnsembleId ensembleId) {
        return ensembleTimers.computeIfAbsent(ensembleId, this::createTimerFor);
    }

    private EnsembleTimer createTimerFor(EnsembleId ensembleId) {
        Ensemble ensemble = ensembleRepository.findById(ensembleId)
                                              .orElseThrow();
        return new EnsembleTimer(ensembleId,
                                 ensemble.name(),
                                 ensemble.participants());
    }

    public boolean hasTimerFor(EnsembleId ensembleId) {
        return ensembleTimers.containsKey(ensembleId);
    }

    public boolean isTimerRunningFor(EnsembleId ensembleId) {
        requireTimerToExistFor(ensembleId);
        return ensembleTimers
                .get(ensembleId)
                .state() == EnsembleTimer.TimerState.RUNNING;
    }

    public void startTimerFor(EnsembleId ensembleId) {
        requireTimerToExistFor(ensembleId);
        ensembleTimers.get(ensembleId)
                      .startTimer();
    }

    private void requireTimerToExistFor(EnsembleId ensembleId) {
        if (!hasTimerFor(ensembleId)) {
            throw new IllegalArgumentException(
                    "No timer for Ensemble ID %d exists.".formatted(ensembleId.id()));
        }
    }

    static class SingletonHashMap<K, V> extends HashMap<K, V> {
        @Override
        public V put(K key, V value) {
            if (this.size() == 1 && !this.containsKey(key)) {
                throw new IllegalStateException("A SingletonHashMap cannot have more than one entry");
            }
            return super.put(key, value);
        }
    }
}
