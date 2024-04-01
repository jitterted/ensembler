package com.jitterted.mobreg.domain;

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


    // unhappy/edge cases:
    //      pause ignored when already paused [no test needed]
    //      resume ignored when already running [no test needed]
    //      pause when not started
    //      pause when finished

}