package com.jitterted.mobreg.application;

import com.jitterted.mobreg.domain.EnsembleId;

import java.time.Instant;

public interface EnsembleTimerTickHandler {
    void handleTickFor(EnsembleId ensembleId, Instant now);
}
