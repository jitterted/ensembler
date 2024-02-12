package com.jitterted.mobreg.adapter.out.clock;

import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.application.port.SecondsTicker;
import com.jitterted.mobreg.domain.EnsembleId;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorSecondsTicker implements SecondsTicker {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final EnsembleTimerHolder ensembleTimerHolder;
    private ScheduledFuture<?> countdownHandle;

    public ScheduledExecutorSecondsTicker(EnsembleTimerHolder ensembleTimerHolder) {
        this.ensembleTimerHolder = ensembleTimerHolder;
    }

    @Override
    public void start(EnsembleId ensembleId) {
        Runnable tickHandlerTask = () ->
                ensembleTimerHolder.handleTickFor(ensembleId, Instant.now());

        countdownHandle = scheduler.scheduleAtFixedRate(tickHandlerTask, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        countdownHandle.cancel(false);
    }
}
