package com.jitterted.mobreg.domain;

import java.time.Duration;
import java.time.Instant;

public class CountdownTimer {
    private final Duration timerDuration;
    private Instant lastTick;
    private Duration timeRemaining;
    private boolean isRunning;

    public CountdownTimer(Duration timerDuration) {
        this.timerDuration = timerDuration;
        this.timeRemaining = timerDuration;
    }

    public void startAt(Instant timeStarted) {
        lastTick = timeStarted;
        isRunning = true;
        tick(timeStarted);
    }

    public void tick(Instant currentTick) {
        if (isRunning) {
            Duration difference = Duration.between(lastTick, currentTick);
            timeRemaining = floorZero(timeRemaining.minus(difference));
        }
        this.lastTick = currentTick;
    }

    public void pause() {
        isRunning = false;
    }

    boolean isFinished() {
        return timeRemaining.isZero();
    }

    public TimeRemaining timeRemaining() {
        return TimeRemaining.from(timeRemaining, timerDuration);
    }

    private Duration floorZero(Duration duration) {
        return duration.isNegative() ? Duration.ZERO : duration;
    }

    @Override
    public String toString() {
        return "CountdownTimer: {" +
               "timerDuration=" + timerDuration +
               ", lastTick=" + lastTick +
               ", timeRemaining=" + timeRemaining +
               '}';
    }
}