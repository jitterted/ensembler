package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class EnsembleTimerTest {

    private static final EnsembleId IRRELEVANT_ENSEMBLE_ID = EnsembleId.of(53);
    private static final MemberId IRRELEVANT_MEMBER_ID = MemberId.of(7);

    @Test
    void newTimerIsWaitingToStart() {
        EnsembleTimer ensembleTimer = createTimer();

        assertThat(ensembleTimer.state())
                .isEqualByComparingTo(EnsembleTimer.TimerState.WAITING_TO_START);
    }

    @Test
    void startedTimerIsRunning() {
        EnsembleTimer ensembleTimer = createTimer();

        ensembleTimer.startTimerAt(Instant.now());

        assertThat(ensembleTimer.state())
                .isEqualByComparingTo(EnsembleTimer.TimerState.RUNNING);
    }

    @Test
    void startTimerThrowsExceptionIfAlreadyRunning() {
        EnsembleTimer ensembleTimer = createTimer();
        ensembleTimer.startTimerAt(Instant.now());

        assertThatIllegalStateException()
                .isThrownBy(() -> ensembleTimer.startTimerAt(Instant.now()))
                .withMessage("Can't Start Timer when Running");
    }

    @Test
    void timerTickWhenWaitingToStartThrowsException() {
        EnsembleTimer ensembleTimer = createTimer();

        assertThatIllegalStateException()
                .isThrownBy(() -> ensembleTimer.tick(Instant.now()));
    }

    @Test
    void isRunningWhenTickTimeBeforeEndTime() {
        EnsembleTimer ensembleTimer = new EnsembleTimer(IRRELEVANT_ENSEMBLE_ID,
                                                        "Test",
                                                        Stream.of(IRRELEVANT_MEMBER_ID),
                                                        Duration.ofMinutes(4));

//        ensembleTimer.startTimerAt(Instant.now())
    }

    // Timer is FINISHED when the tick is on or later than its internal "end time"
    // COMMAND: timer.tick(now)
    // QUERY: timer.hasFinished() --> timer.state(): WAITING_TO_START, RUNNING, FINISHED

    // Test that throws exception if startTimer() called when in FINISHED state

    private EnsembleTimer createTimer() {
        return new EnsembleTimer(IRRELEVANT_ENSEMBLE_ID, "Test", Stream.of(IRRELEVANT_MEMBER_ID));
    }
}