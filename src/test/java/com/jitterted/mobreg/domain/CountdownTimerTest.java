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

        Instant halfway = timerStartedAt.plus(Duration.ofSeconds(100));
        countdownTimer.tick(halfway);

        assertThat(countdownTimer.timeRemaining())
                .isEqualTo(new TimeRemaining(1, 40, 50.0));
    }
}