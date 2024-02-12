package com.jitterted.mobreg.application.port;

import com.jitterted.mobreg.domain.EnsembleId;

public interface SecondsTicker {
    void start(EnsembleId ensembleId);

    void stop();
}
