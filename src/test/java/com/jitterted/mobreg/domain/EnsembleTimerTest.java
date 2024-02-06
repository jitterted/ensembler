package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class EnsembleTimerTest {

    @Test
    void newTimerIsWaitingToStart() {
        EnsembleTimer ensembleTimer = createTimer();

        assertThat(ensembleTimer.state())
                .isEqualByComparingTo(EnsembleTimer.TimerState.WAITING_TO_START);
    }

    @Test
    void startedTimerIsRunning() {
        EnsembleTimer ensembleTimer = createTimer();

        ensembleTimer.startTimer();

        assertThat(ensembleTimer.state())
                .isEqualByComparingTo(EnsembleTimer.TimerState.RUNNING);
    }

    @Test
    void startTimerThrowsExceptionIfAlreadyRunning() {
        EnsembleTimer ensembleTimer = createTimer();
        ensembleTimer.startTimer();

        assertThatIllegalStateException()
                .isThrownBy(ensembleTimer::startTimer)
                .withMessage("Can't Start Timer when Running");
    }

    @Test
    void timerTickWhenWaitingToStartThrowsException() {
        EnsembleTimer ensembleTimer = createTimer();

        assertThatIllegalStateException()
                .isThrownBy(() -> ensembleTimer.tick(Instant.now()));
    }

    //    @Test
//    void whenNowIsBeforeTimerEndTimeThenStillRunning() {
//
//    }

    // Timer is FINISHED when the tick is on or later than its internal "end time"
    // COMMAND: timer.tick(now)
    // QUERY: timer.hasFinished() --> timer.state(): WAITING_TO_START, RUNNING, FINISHED

    // Test that throws exception if startTimer() called when in FINISHED state

    private EnsembleTimer createTimer() {
        return new EnsembleTimer(EnsembleId.of(53), "Test", Stream.of(MemberId.of(7)));
    }
}