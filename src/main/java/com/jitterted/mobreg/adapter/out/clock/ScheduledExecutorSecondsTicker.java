package com.jitterted.mobreg.adapter.out.clock;

import com.jitterted.mobreg.application.EnsembleTimerTickHandler;
import com.jitterted.mobreg.application.port.SecondsTicker;
import com.jitterted.mobreg.domain.EnsembleId;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorSecondsTicker implements SecondsTicker {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> countdownHandle;
    private EnsembleId countdownEnsembleId;

    public ScheduledExecutorSecondsTicker() {
    }

    @Override
    public void start(EnsembleId ensembleId, EnsembleTimerTickHandler ensembleTimerTickHandler) {
        if (countdownHandle != null) {
            throw new IllegalStateException("Countdown timer already scheduled for " + countdownEnsembleId);
        }
        Runnable tickHandlerTask = () ->
                ensembleTimerTickHandler.handleTickFor(ensembleId, Instant.now());

        countdownHandle = scheduler.scheduleAtFixedRate(tickHandlerTask, 0, 1, TimeUnit.SECONDS);
        countdownEnsembleId = ensembleId;
    }

    @Override
    public void stop() {
        countdownHandle.cancel(false);
        countdownHandle = null;
    }
}
