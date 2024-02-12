package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.SecondsTicker;
import com.jitterted.mobreg.domain.EnsembleId;

public class DoNothingSecondsTicker implements SecondsTicker {
    @Override
    public void start(EnsembleId ensembleId, EnsembleTimerTickHandler ensembleTimerTickHandler) {}

    @Override
    public void stop() {}
}
