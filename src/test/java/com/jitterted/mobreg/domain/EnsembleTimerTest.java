package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class EnsembleTimerTest {

    @Test
    void newTimerIsNotRunning() {
        EnsembleTimer ensembleTimer = createTimer();

        assertThat(ensembleTimer.hasTimerStarted())
                .isFalse();
    }

    @Test
    void startedTimerIsRunning() {
        EnsembleTimer ensembleTimer = createTimer();

        ensembleTimer.startTimer();

        assertThat(ensembleTimer.hasTimerStarted())
                .isTrue();
    }

    @Test
    void startTimerThrowsExceptionIfAlreadyRunning() {
        EnsembleTimer ensembleTimer = createTimer();
        ensembleTimer.startTimer();

        assertThatIllegalStateException()
                .isThrownBy(ensembleTimer::startTimer)
                .withMessage("Can't Start Timer when Already Started");
    }

    // how much time is left?
    // determines whether the timer is FINISHED or not


    private EnsembleTimer createTimer() {
        return new EnsembleTimer(EnsembleId.of(53), "Test", Stream.of(MemberId.of(7)));
    }
}