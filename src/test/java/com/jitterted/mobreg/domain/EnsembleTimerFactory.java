package com.jitterted.mobreg.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class EnsembleTimerFactory {
    public static final EnsembleId IRRELEVANT_ENSEMBLE_ID = EnsembleId.of(53);
    private static final MemberId IRRELEVANT_MEMBER_ID = MemberId.of(7);
    public static final String IRRELEVANT_NAME = "Test";

    public static Fixture create4MinuteTimerInFinishedState() {
        EnsembleTimer ensembleTimer = createTimerWith4MinuteDuration();
        Instant timerStartedAt = Instant.now();
        ensembleTimer.startTimerAt(timerStartedAt);
        ensembleTimer.tick(timerStartedAt.plus(Duration.ofMinutes(4).plusMillis(1)));
        return new Fixture(ensembleTimer, timerStartedAt);
    }

    public static EnsembleTimer createTimerWith4MinuteDuration() {
        return new EnsembleTimer(IRRELEVANT_ENSEMBLE_ID,
                                 IRRELEVANT_NAME,
                                 List.of(IRRELEVANT_MEMBER_ID),
                                 Duration.ofMinutes(4));
    }

    public static EnsembleTimer createTimer() {
        return new EnsembleTimer(IRRELEVANT_ENSEMBLE_ID,
                                 IRRELEVANT_NAME,
                                 List.of(IRRELEVANT_MEMBER_ID));
    }

    public record Fixture(EnsembleTimer ensembleTimer, Instant timerStartedAt) {
    }
}
