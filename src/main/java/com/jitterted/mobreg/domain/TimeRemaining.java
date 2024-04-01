package com.jitterted.mobreg.domain;

import java.time.Duration;

public record TimeRemaining(int minutes, int seconds, double percent) {

    public static TimeRemaining from(Duration timeRemaining, Duration totalDuration) {
        int remainingMinutes = (int) timeRemaining.toMinutes();
        int remainingSeconds = Math.max(0, (int) timeRemaining.minusMinutes(remainingMinutes)
                                                              .getSeconds());
        double percentRemaining = Math.max(0,
                                           timeRemaining.toMillis() * 100.0
                                           / totalDuration.toMillis());
        return new TimeRemaining(remainingMinutes, remainingSeconds, percentRemaining);
    }
}
