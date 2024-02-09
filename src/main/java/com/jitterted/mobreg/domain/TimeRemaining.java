package com.jitterted.mobreg.domain;

import java.time.Duration;
import java.time.Instant;

public record TimeRemaining(int minutes, int seconds, int percent) {

    static TimeRemaining whileRunning(Instant lastTick, Instant timerEnd, Duration timerDuration) {
        Duration remainingDuration = Duration.between(lastTick, timerEnd);
        int remainingMinutes = (int) remainingDuration.toMinutes();
        int remainingSeconds = Math.max(0, (int) remainingDuration.minusMinutes(remainingMinutes)
                                                                  .getSeconds());
        int percentRemaining = (int) (remainingDuration.toMillis() * 100 / timerDuration.toMillis());
        return new TimeRemaining(remainingMinutes, remainingSeconds, percentRemaining);
    }

    static TimeRemaining beforeStarted(Duration timerDuration) {
        return new TimeRemaining(timerDuration.toMinutesPart(),
                                 timerDuration.toSecondsPart(),
                                 100);
    }
}
