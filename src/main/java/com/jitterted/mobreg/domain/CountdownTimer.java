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

    public Duration getTimerDuration() {
        return timerDuration;
    }

    public Instant getTimerEnd() {
        return timerEnd;
    }

    public Instant getLastTick() {
        return lastTick;
    }

    public void tick(Instant lastTick) {
        this.lastTick = lastTick;
    }

    public void startAt(Instant timeStarted) {
        tick(timeStarted);
        timerEnd = timeStarted.plus(getTimerDuration());
    }
}