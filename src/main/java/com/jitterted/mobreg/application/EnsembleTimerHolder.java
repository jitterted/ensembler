package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.Broadcaster;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.HashMap;

public class EnsembleTimerHolder {
    private final EnsembleRepository ensembleRepository;
    private final Broadcaster broadcaster;
    private final SingleEntryHashMap<EnsembleId, EnsembleTimer> ensembleTimers = new SingleEntryHashMap<>();

    @Deprecated // Must use the constructor that takes a Broadcaster implementation
    public EnsembleTimerHolder(EnsembleRepository ensembleRepository) {
        this.ensembleRepository = ensembleRepository;
        this.broadcaster = ensembleTimer -> {};
    }

    public EnsembleTimerHolder(EnsembleRepository ensembleRepository, Broadcaster broadcaster) {
        this.ensembleRepository = ensembleRepository;
        this.broadcaster = broadcaster;
    }

    @NotNull
    public EnsembleTimer timerFor(EnsembleId ensembleId) {
        if (!ensembleTimers.containsKey(ensembleId)) {
            throw new IllegalStateException("No Ensemble Timer exists for Ensemble %d.".formatted(ensembleId.id()));
        }
        return ensembleTimers.get(ensembleId);
    }

    @NotNull
    public EnsembleTimer createTimerFor(EnsembleId ensembleId) {
        Ensemble ensemble = ensembleRepository.findById(ensembleId)
                                              .orElseThrow();
        EnsembleTimer ensembleTimer = new EnsembleTimer(ensembleId,
                                                        ensemble.name(),
                                                        ensemble.participants());
        ensembleTimers.put(ensembleId, ensembleTimer);
        broadcaster.sendCurrentTimer(ensembleTimer);
        return ensembleTimer;
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

    public void startTimerFor(EnsembleId ensembleId, Instant timeStarted) {
        requireTimerToExistFor(ensembleId);
        ensembleTimers.get(ensembleId)
                      .startTimerAt(timeStarted);
    }

    public void handleTickFor(EnsembleId ensembleId, Instant now) {
        EnsembleTimer ensembleTimer = timerFor(ensembleId);
        ensembleTimer.tick(now);
        broadcaster.sendCurrentTimer(ensembleTimer);
    }


    private void requireTimerToExistFor(EnsembleId ensembleId) {
        if (!hasTimerFor(ensembleId)) {
            throw new IllegalArgumentException(
                    "No timer for Ensemble ID %d exists.".formatted(ensembleId.id()));
        }
    }

    static class SingleEntryHashMap<K, V> extends HashMap<K, V> {
        @Override
        public V put(K key, V value) {
            if (this.size() == 1 && !this.containsKey(key)) {
                throw new IllegalStateException("A SingleEntryHashMap cannot have more than one entry, has entry for %s, attempting to add entry for %s"
                                                        .formatted(keySet().iterator().next(), key));
            }
            return super.put(key, value);
        }
    }
}
