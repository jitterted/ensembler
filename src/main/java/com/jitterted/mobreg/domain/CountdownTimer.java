package com.jitterted.mobreg.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.StringJoiner;

import static com.jitterted.mobreg.domain.CountdownTimer.TimerState.FINISHED;
import static com.jitterted.mobreg.domain.CountdownTimer.TimerState.PAUSED;
import static com.jitterted.mobreg.domain.CountdownTimer.TimerState.RUNNING;
import static com.jitterted.mobreg.domain.CountdownTimer.TimerState.WAITING_TO_START;

public class CountdownTimer {
    private final Duration timerDuration;
    private Instant lastTick;
    private Duration timeRemaining;
    private TimerState timerState;

    public CountdownTimer(Duration timerDuration) {
        this.timerDuration = timerDuration;
        this.timeRemaining = timerDuration;
        this.timerState = WAITING_TO_START;
    }

    public void startAt(Instant timeStarted) {
        requireWaitingToStart();

        lastTick = timeStarted;
        timerState = RUNNING;
        tick(timeStarted);
    }

    public void tick(Instant currentTick) {
        requireRunningOrPaused(currentTick);

        updateTimeRemaining(currentTick);
        this.lastTick = currentTick;
    }

    public void updateTimeRemaining(Instant currentTick) {
        if (timerState == RUNNING) {
            Duration difference = Duration.between(lastTick, currentTick);
            difference = difference.multipliedBy(30);
            timeRemaining = floorZero(timeRemaining.minus(difference));
            timerState = timeRemaining.isZero() ? FINISHED : RUNNING;
        }
    }

    public void pause() {
        timerState = PAUSED;
    }

    public void resume() {
        timerState = RUNNING;
    }

    public TimeRemaining timeRemaining() {
        return TimeRemaining.from(timeRemaining, timerDuration);
    }

    private Duration floorZero(Duration duration) {
        return duration.isNegative() ? Duration.ZERO : duration;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CountdownTimer.class.getSimpleName() + "[", "]")
                .add("timerDuration=" + timerDuration)
                .add("lastTick=" + lastTick)
                .add("timeRemaining=" + timeRemaining)
                .add("timerState=" + timerState)
                .toString();
    }

    public TimerState state() {
        return timerState;
    }

    private void requireRunningOrPaused(Instant now) {
        switch (timerState) {
            case FINISHED ->
                    throw new IllegalStateException("Tick received at %s after Timer already Finished: %s."
                                                            .formatted(now, this));
            case WAITING_TO_START ->
                    throw new IllegalStateException("Timer is Waiting to Start, but Tick was received at %s."
                                                            .formatted(now));
        }
    }

    private void requireWaitingToStart() {
        if (timerState != WAITING_TO_START) {
            throw new IllegalStateException("Can't Start Timer when " + timerState);
        }
    }

    public enum TimerState {
        WAITING_TO_START,
        RUNNING,
        PAUSED,
        FINISHED
    }

}