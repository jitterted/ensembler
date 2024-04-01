package com.jitterted.mobreg.domain;

import java.time.Duration;
import java.time.Instant;

public class CountdownTimer {
    private final Duration timerDuration;
    private Instant lastTick;
    @Deprecated // this will be replaced by a "bag of time"
    private Instant timerEnd;
    private Duration timeRemaining;

    public CountdownTimer(Duration timerDuration) {
        this.timerDuration = timerDuration;
        this.timeRemaining = timerDuration;
    }

    public void tick(Instant currentTick) {
        Duration difference = Duration.between(lastTick, currentTick);
        timeRemaining = timeRemaining.minus(difference);
        this.lastTick = currentTick;
    }

    public void startAt(Instant timeStarted) {
        lastTick = timeStarted;
        tick(timeStarted);
        timerEnd = timeStarted.plus(timerDuration);
    }

    boolean isFinished(Instant now) {
        return !now.isBefore(timerEnd);
    }

    public TimeRemaining timeRemaining() {
        return TimeRemaining.from(timeRemaining, timerDuration);
    }

    @Override
    public String toString() {
        return "CountdownTimer {" +
               "timerDuration=" + timerDuration +
               ", lastTick=" + lastTick +
               ", timerEnd=" + timerEnd +
               '}';
    }
}