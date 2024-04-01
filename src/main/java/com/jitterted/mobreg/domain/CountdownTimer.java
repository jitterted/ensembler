package com.jitterted.mobreg.domain;

import java.time.Duration;
import java.time.Instant;

public class CountdownTimer {
    private final Duration timerDuration;
    private Instant lastTick;
    private Instant timerEnd;

    public CountdownTimer(Duration timerDuration) {
        this.timerDuration = timerDuration;
    }

    public void tick(Instant currentTick) {
        this.lastTick = currentTick;
    }

    public void startAt(Instant timeStarted) {
        tick(timeStarted);
        timerEnd = timeStarted.plus(timerDuration);
    }

    boolean isFinished(Instant now) {
        return !now.isBefore(timerEnd);
    }

    public TimeRemaining timeRemaining() {
        if (timerEnd == null) {
            return TimeRemaining.beforeStarted(timerDuration);
        }
        return TimeRemaining.whileRunning(lastTick, timerEnd, timerDuration);
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