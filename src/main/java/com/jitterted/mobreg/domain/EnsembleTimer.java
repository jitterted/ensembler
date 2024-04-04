package com.jitterted.mobreg.domain;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

import static com.jitterted.mobreg.domain.CountdownTimer.TimerState.FINISHED;

public class EnsembleTimer {
    public static final Duration DEFAULT_TIMER_DURATION = Duration.ofMinutes(4);

    private final EnsembleId ensembleId;
    private final String ensembleName;
    private final List<Member> participants;
    private final Rotation rotation;
    private CountdownTimer turnTimer;
    private final Duration turnDuration;

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
    public CountdownTimer.TimerState state() {
        return turnTimer.state();
    }

    public void startTimerAt(Instant timeStarted) {
        turnTimer.startAt(timeStarted);
    }

    public void tick(Instant now) {
        turnTimer.tick(now);
    }

    public TimeRemaining timeRemaining() {
        return turnTimer.timeRemaining();
    }

    public Rotation rotation() {
        return rotation;
    }

    public void rotateRoles() {
        requireFinished();
        rotation.rotate();
        turnTimer = new CountdownTimer(turnDuration);
    }

    public void pause() {
        turnTimer.pause();
    }

    private void requireFinished() {
        if (turnTimer.state() != FINISHED) {
            throw new IllegalStateException("Can't Rotate when timer state is %s".formatted(turnTimer.state()));
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EnsembleTimer.class.getSimpleName() + "[", "]")
                .add("ensembleId=" + ensembleId)
                .add("ensembleName='" + ensembleName + "'")
                .add("rotation=" + rotation)
                .add("turnTimer=" + turnTimer)
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
