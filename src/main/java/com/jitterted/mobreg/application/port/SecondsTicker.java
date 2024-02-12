package com.jitterted.mobreg.application.port;

import com.jitterted.mobreg.application.EnsembleTimerTickHandler;
import com.jitterted.mobreg.domain.EnsembleId;

public interface SecondsTicker {
    void start(EnsembleId ensembleId, EnsembleTimerTickHandler ensembleTimerTickHandler);

    void stop();
}
