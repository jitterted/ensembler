package com.jitterted.mobreg.domain;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

public class EnsembleTimer {
    public static final Duration DEFAULT_TIMER_DURATION = Duration.ofMinutes(4);

    private final EnsembleId ensembleId;
    private final String ensembleName;
    private final List<MemberId> participants;
    private final Rotation rotation;

    private TimerState currentState;
    private final Duration timerDuration;
    private Instant timerEnd;
    private Instant lastTick;

    public EnsembleTimer(EnsembleId ensembleId,
                         String ensembleName,
                         List<MemberId> participants) {
        this(ensembleId, ensembleName, participants, DEFAULT_TIMER_DURATION);
    }

    public EnsembleTimer(EnsembleId ensembleId,
                         String ensembleName,
                         List<MemberId> participants,
                         Duration timerDuration) {
        this.ensembleId = ensembleId;
        this.ensembleName = ensembleName;
        this.participants = participants;
        this.timerDuration = timerDuration;
        this.currentState = TimerState.WAITING_TO_START;
        rotation = new Rotation(participants);
    }

    public EnsembleId ensembleId() {
        return ensembleId;
    }

    public Stream<MemberId> participants() {
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
        lastTick = timeStarted;
        timerEnd = timeStarted.plus(timerDuration);
        currentState = TimerState.RUNNING;
    }

    public void tick(Instant now) {
        requireRunning(now);
        lastTick = now;
        if (isAtOrAfterTimerEnd(now)) {
            currentState = TimerState.FINISHED;
        }
    }

    public TimeRemaining timeRemaining() {
        return switch (currentState) {
            case WAITING_TO_START -> TimeRemaining.beforeStarted(timerDuration);
            case RUNNING, FINISHED -> TimeRemaining.whileRunning(lastTick, timerEnd, timerDuration);
        };
    }

    private void requireRunning(Instant now) {
        switch (currentState) {
            case FINISHED ->
                    throw new IllegalStateException("Tick received at %s after Timer already Finished at %s."
                                                            .formatted(now, timerEnd));
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

    private boolean isAtOrAfterTimerEnd(Instant now) {
        return !now.isBefore(timerEnd);
    }

    public Rotation rotation() {
        return rotation;
    }

    public void rotateRoles() {
        requireFinished();
        rotation.rotate();
        currentState = TimerState.WAITING_TO_START;
    }

    private void requireFinished() {
        if (currentState == TimerState.WAITING_TO_START
                || currentState == TimerState.RUNNING) {
            throw new IllegalStateException("Can't Rotate when timer state is %s".formatted(currentState));
        }
    }

    public enum TimerState {
        WAITING_TO_START,
        RUNNING,
        FINISHED
    }

    @Override
    public String toString() {
        return MessageFormat.format(
                "EnsembleTimer [ensembleId={0}, participants={1}]",
                ensembleId, participants);
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
