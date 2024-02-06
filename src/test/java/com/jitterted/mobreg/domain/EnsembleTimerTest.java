package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class EnsembleTimerTest {

    private static final EnsembleId IRRELEVANT_ENSEMBLE_ID = EnsembleId.of(53);
    private static final MemberId IRRELEVANT_MEMBER_ID = MemberId.of(7);
    private static final String IRRELEVANT_NAME = "Test";

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
        EnsembleTimer ensembleTimer = createTimerWith4MinuteDuration();
        Instant timerStartedAt = Instant.now();
        ensembleTimer.startTimerAt(timerStartedAt);

        Instant oneMilliBeforeEnd = timerStartedAt.plus(Duration.ofMinutes(4).minusMillis(1));
        ensembleTimer.tick(oneMilliBeforeEnd);

        assertThat(ensembleTimer.state())
                .isEqualByComparingTo(EnsembleTimer.TimerState.RUNNING);
    }

    @Test
    void isFinishedWhenTickTimeAtEndTime() {
        EnsembleTimer ensembleTimer = createTimerWith4MinuteDuration();
        Instant timerStartedAt = Instant.now();
        ensembleTimer.startTimerAt(timerStartedAt);

        Instant timerFinishedAt = timerStartedAt.plus(Duration.ofMinutes(4));
        ensembleTimer.tick(timerFinishedAt);

        assertThat(ensembleTimer.state())
                .isEqualByComparingTo(EnsembleTimer.TimerState.FINISHED);
    }

    @Test
    void isFinishedWhenTickTimeAfterEndTime() {
        EnsembleTimer ensembleTimer = createTimerWith4MinuteDuration();
        Instant timerStartedAt = Instant.now();
        ensembleTimer.startTimerAt(timerStartedAt);

        Instant oneMilliAfterEnd = timerStartedAt.plus(Duration.ofMinutes(4).plusMillis(1));
        ensembleTimer.tick(oneMilliAfterEnd);

        assertThat(ensembleTimer.state())
                .isEqualByComparingTo(EnsembleTimer.TimerState.FINISHED);
    }

    @Test
    void tickWhenFinishedThrowsException() {
        Fixture fixture = create4MinuteTimerInFinishedState();

        Instant finishedAt = fixture.timerStartedAt().plus(Duration.ofMinutes(4));
        Instant finishedAtPlus20Millis = finishedAt.plusMillis(20);
        assertThatIllegalStateException()
                .isThrownBy(() -> fixture.ensembleTimer().tick(finishedAtPlus20Millis))
                .withMessage("Tick received at %s after Timer already Finished at %s."
                                     .formatted(finishedAtPlus20Millis, finishedAt));
    }




    // -- ENCAPSULATED SETUP

    private Fixture create4MinuteTimerInFinishedState() {
        EnsembleTimer ensembleTimer = createTimerWith4MinuteDuration();
        Instant timerStartedAt = Instant.now();
        ensembleTimer.startTimerAt(timerStartedAt);
        ensembleTimer.tick(timerStartedAt.plus(Duration.ofMinutes(4).plusMillis(1)));
        return new Fixture(ensembleTimer, timerStartedAt);
    }

    private record Fixture(EnsembleTimer ensembleTimer, Instant timerStartedAt) {
    }

    private EnsembleTimer createTimerWith4MinuteDuration() {
        return new EnsembleTimer(IRRELEVANT_ENSEMBLE_ID,
                                 IRRELEVANT_NAME,
                                 Stream.of(IRRELEVANT_MEMBER_ID),
                                 Duration.ofMinutes(4));
    }

    private EnsembleTimer createTimer() {
        return new EnsembleTimer(IRRELEVANT_ENSEMBLE_ID, IRRELEVANT_NAME, Stream.of(IRRELEVANT_MEMBER_ID));
    }
}