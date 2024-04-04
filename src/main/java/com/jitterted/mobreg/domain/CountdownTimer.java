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
        lastTick = timeStarted;
        timerState = RUNNING;
        tick(timeStarted);
    }

    public void tick(Instant currentTick) {
        updateTimeRemaining(currentTick);
        this.lastTick = currentTick;
    }

    public void updateTimeRemaining(Instant currentTick) {
        if (timerState == RUNNING) {
            Duration difference = Duration.between(lastTick, currentTick);
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

    boolean isFinished() {
        return timerState == TimerState.FINISHED;
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

    enum TimerState {
        WAITING_TO_START,
        RUNNING,
        PAUSED,
        FINISHED
    }
}