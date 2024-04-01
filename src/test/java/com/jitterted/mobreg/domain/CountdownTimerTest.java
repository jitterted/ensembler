package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Disabled;
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
    @Disabled
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

    // unhappy:
    //      pause when not started
    //      pause when finished

}