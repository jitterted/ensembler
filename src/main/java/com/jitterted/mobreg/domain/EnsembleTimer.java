package com.jitterted.mobreg.domain;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

public class EnsembleTimer {
    private static final Duration DEFAULT_TIMER_DURATION = Duration.ofMinutes(4);

    private final EnsembleId ensembleId;
    private final String ensembleName;
    private final Stream<MemberId> participants;

    private TimerState currentState;
    private final Duration timerDuration;
    private Instant timerEnd;
    private Instant lastTick;

    public EnsembleTimer(EnsembleId ensembleId,
                         String ensembleName,
                         Stream<MemberId> participants) {
        this(ensembleId, ensembleName, participants, DEFAULT_TIMER_DURATION);
    }

    public EnsembleTimer(EnsembleId ensembleId, String ensembleName, Stream<MemberId> participants, Duration timerDuration) {
        this.ensembleId = ensembleId;
        this.ensembleName = ensembleName;
        this.participants = participants;
        this.timerDuration = timerDuration;
        this.currentState = TimerState.WAITING_TO_START;
    }

    public EnsembleId ensembleId() {
        return ensembleId;
    }

    public Stream<MemberId> participants() {
        return participants;
    }

    public String ensembleName() {
        return ensembleName;
    }

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
        if (currentState == TimerState.WAITING_TO_START) {
            return new TimeRemaining(timerDuration.toMinutesPart(),
                                     timerDuration.toSecondsPart(),
                                     100);
        }
        return computeRunningTimeRemaining();
    }

    private TimeRemaining computeRunningTimeRemaining() {
        Duration remainingDuration = Duration.between(lastTick, timerEnd);
        int remainingMinutes = (int) remainingDuration.toMinutes();
        int remainingSeconds = (int) remainingDuration.minusMinutes(remainingMinutes).getSeconds();
        int percentRemaining = (int) (remainingDuration.toMillis() * 100 / timerDuration.toMillis());
        return new TimeRemaining(remainingMinutes, remainingSeconds, percentRemaining);
    }

    private void requireRunning(Instant now) {
        if (currentState != TimerState.RUNNING) {
            throw new IllegalStateException("Tick received at %s after Timer already Finished at %s."
                                                    .formatted(now, timerEnd));
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
