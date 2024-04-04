package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class CountdownTimerTest {

    @Test
    void timeRemainingForNewUnstartedTimerIsInitialDuration() {
        CountdownTimer countdownTimer = new CountdownTimer(Duration.ofSeconds(90));

        TimeRemaining timeRemaining = countdownTimer.timeRemaining();

        assertThat(timeRemaining)
                .isEqualTo(new TimeRemaining(1, 30, 100.0));
    }

    @Test
    void timeRemainingForStartedTimerIsTimeRemainingAfterTick() {
        CountdownTimer countdownTimer = new CountdownTimer(Duration.ofSeconds(200));
        Instant timerStartedAt = Instant.now();
        countdownTimer.startAt(timerStartedAt);

        Instant halfway = timerStartedAt.plusSeconds(100);
        countdownTimer.tick(halfway);

        assertThat(countdownTimer.timeRemaining())
                .isEqualTo(new TimeRemaining(1, 40, 50.0));
    }

    @Test
    void timeRemainingIsUnaffectedByTickDuringPause() {
        CountdownTimer countdownTimer = new CountdownTimer(Duration.ofSeconds(50));
        Instant timerStartedAt = Instant.now();
        countdownTimer.startAt(timerStartedAt);
        countdownTimer.tick(timerStartedAt.plusSeconds(10));

        countdownTimer.pause();
        countdownTimer.tick(timerStartedAt.plusSeconds(11));
        countdownTimer.tick(timerStartedAt.plusSeconds(12));

        assertThat(countdownTimer.timeRemaining().seconds())
                .isEqualTo(40);
    }

    @Test
    void timeRemainingIsOnlyReducedDuringTimerRunning() {
        CountdownTimer countdownTimer = new CountdownTimer(Duration.ofSeconds(50));
        Instant timerStartedAt = Instant.now();
        countdownTimer.startAt(timerStartedAt);
        countdownTimer.tick(timerStartedAt.plusSeconds(10)); // -10 sec
        countdownTimer.pause();
        countdownTimer.tick(timerStartedAt.plusSeconds(11)); // ignored
        countdownTimer.tick(timerStartedAt.plusSeconds(12)); // ignored

        countdownTimer.resume();
        countdownTimer.tick(timerStartedAt.plusSeconds(13)); // -1 sec
        countdownTimer.tick(timerStartedAt.plusSeconds(14)); // -1 sec

        assertThat(countdownTimer.timeRemaining().seconds())
                .as("Expected 38 seconds left on the timer")
                .isEqualTo(50 - 10 - 1 - 1);

    }

    @Nested
    class UnhappyScenarios {

        @Test
        void exceptionThrownIfStartTimerWhenRunning() {
            CountdownTimer countdownTimer = new CountdownTimer(EnsembleTimer.DEFAULT_TIMER_DURATION);
            countdownTimer.startAt(Instant.now());

            assertThatIllegalStateException()
                    .isThrownBy(() -> countdownTimer.startAt(Instant.now()))
                    .withMessage("Can't Start Timer when RUNNING");
        }

        @Test
        void exceptionThrownIfStartTimerWhenPaused() {
            CountdownTimer countdownTimer = new CountdownTimer(EnsembleTimer.DEFAULT_TIMER_DURATION);
            countdownTimer.startAt(Instant.now());
            countdownTimer.pause();

            assertThatIllegalStateException()
                    .isThrownBy(() -> countdownTimer.startAt(Instant.now()))
                    .withMessage("Can't Start Timer when PAUSED");
        }

        @Test
        void exceptionThrownIfStartTimerWhenFinished() {
            Fixture fixture = createFinishedTimer();

            assertThatIllegalStateException()
                    .isThrownBy(() -> fixture.countdownTimer().startAt(Instant.now()))
                    .withMessage("Can't Start Timer when FINISHED");
        }

        @Test
        void timerTickWhenWaitingToStartThrowsException() {
            CountdownTimer countdownTimer = new CountdownTimer(EnsembleTimer.DEFAULT_TIMER_DURATION);

            Instant tickAtNow = Instant.now();
            assertThatIllegalStateException()
                    .isThrownBy(() -> countdownTimer.tick(tickAtNow))
                    .withMessage("Timer is Waiting to Start, but Tick was received at %s."
                                         .formatted(tickAtNow));
        }

        @Test
        void tickWhenFinishedThrowsException() {
            Fixture fixture = createFinishedTimer();

            Instant finishedAt = fixture.timerStartedAt().plus(Duration.ofMinutes(4));
            Instant finishedAtPlus20Millis = finishedAt.plusMillis(20);
            assertThatIllegalStateException()
                    .isThrownBy(() -> fixture.countdownTimer().tick(finishedAtPlus20Millis))
                    .withMessageStartingWith("Tick received at %s after Timer already Finished:"
                                                     .formatted(finishedAtPlus20Millis));
        }

        private Fixture createFinishedTimer() {
            CountdownTimer countdownTimer = new CountdownTimer(EnsembleTimer.DEFAULT_TIMER_DURATION);
            Instant timerStartedAt = Instant.now();
            countdownTimer.startAt(timerStartedAt);
            countdownTimer.tick(timerStartedAt.plus(Duration.ofMinutes(4).plusMillis(1)));
            return new Fixture(countdownTimer, timerStartedAt);
        }

        private record Fixture(CountdownTimer countdownTimer, Instant timerStartedAt) {
        }

        @Test
        @Disabled("Waiting for CountdownTimer to know its state")
        void exceptionThrownOnPauseWhenTimerNotStarted() {
            // implement me
        }

        @Test
        @Disabled("Waiting for CountdownTimer to know its state")
        void exceptionThrownOnPauseWhenTimerFinished() {
            // implement me
        }
    }

}