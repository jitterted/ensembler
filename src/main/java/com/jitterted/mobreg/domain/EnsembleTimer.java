package com.jitterted.mobreg.domain;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

public class EnsembleTimer {
    public static final Duration DEFAULT_TIMER_DURATION = Duration.ofMinutes(4);

    private final EnsembleId ensembleId;
    private final String ensembleName;
    private final List<Member> participants;
    private final Rotation rotation;
    private CountdownTimer turnTimer;
    private final Duration turnDuration;
    private TimerState currentState; // TODO: push into CountdownTimer

    public EnsembleTimer(EnsembleId ensembleId,
                         String ensembleName,
                         List<Member> participants) {
        this(ensembleId, ensembleName, participants, DEFAULT_TIMER_DURATION);
    }

    public EnsembleTimer(EnsembleId ensembleId,
                         String ensembleName,
                         List<Member> participants,
                         Duration turnDuration) {
        this.ensembleId = ensembleId;
        this.ensembleName = ensembleName;
        this.participants = participants;
        this.turnDuration = turnDuration;
        this.turnTimer = new CountdownTimer(turnDuration);
        this.currentState = TimerState.WAITING_TO_START;
        this.rotation = new Rotation(participants);
    }

    public EnsembleId ensembleId() {
        return ensembleId;
    }

    public Stream<Member> participants() {
        return participants.stream();
    }

    public String ensembleName() {
        return ensembleName;
    }

    @NotNull
    public TimerState state() {
        return currentState;
    }

    public void startTimerAt(Instant timeStarted) {
        requireNotRunning();
        turnTimer.startAt(timeStarted);
        currentState = TimerState.RUNNING;
    }

    public void tick(Instant now) {
        requireRunning(now);
        turnTimer.tick(now);
        if (turnTimer.isFinished()) {
            currentState = TimerState.FINISHED;
        }
    }

    public TimeRemaining timeRemaining() {
        return turnTimer.timeRemaining();
    }

    private void requireRunning(Instant now) {
        switch (currentState) {
            case FINISHED ->
                    throw new IllegalStateException("Tick received at %s after Timer already Finished: %s."
                                                            .formatted(now, turnTimer));
            case WAITING_TO_START ->
                    throw new IllegalStateException("Timer is Waiting to Start, but Tick was received at %s."
                                                            .formatted(now));
        }
    }

    private void requireNotRunning() {
        if (currentState == TimerState.RUNNING) {
            throw new IllegalStateException("Can't Start Timer when Running");
        }
    }

    public Rotation rotation() {
        return rotation;
    }

    public void rotateRoles() {
        requireFinished();
        rotation.rotate();
        turnTimer = new CountdownTimer(turnDuration);
        currentState = TimerState.WAITING_TO_START;
    }

    private void requireFinished() {
        if (currentState == TimerState.WAITING_TO_START
                || currentState == TimerState.RUNNING) {
            throw new IllegalStateException("Can't Rotate when timer state is %s".formatted(currentState));
        }
    }

    public void pause() {
        turnTimer.pause();
        currentState = TimerState.PAUSED;
    }

    public enum TimerState {
        WAITING_TO_START,
        RUNNING,
        PAUSED,
        FINISHED
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EnsembleTimer.class.getSimpleName() + "[", "]")
                .add("ensembleId=" + ensembleId)
                .add("ensembleName='" + ensembleName + "'")
                .add("rotation=" + rotation)
                .add("turnTimer=" + turnTimer)
                .add("currentState=" + currentState)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EnsembleTimer that = (EnsembleTimer) o;

        return ensembleId.equals(that.ensembleId);
    }

    @Override
    public int hashCode() {
        return ensembleId.hashCode();
    }
}
