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

    @Deprecated
    public void setTimerEnd(Instant timerEnd) {
        this.timerEnd = timerEnd;
    }

    public Instant getLastTick() {
        return lastTick;
    }

    @Deprecated
    public void setLastTick(Instant lastTick) {
        this.lastTick = lastTick;
    }
}