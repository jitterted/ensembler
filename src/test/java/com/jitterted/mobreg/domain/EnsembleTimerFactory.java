package com.jitterted.mobreg.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class EnsembleTimerFactory {
    public static final EnsembleId IRRELEVANT_ENSEMBLE_ID = EnsembleId.of(53);
    public static final String IRRELEVANT_NAME = "Test";
    private static final String IRRELEVANT_GITHUB_USERNAME = "irrelevant";

    public static Fixture create4MinuteTimerInFinishedState() {
        EnsembleTimer ensembleTimer = createTimerWith4MinuteDuration();
        Instant timerStartedAt = Instant.now();
        ensembleTimer.startTimerAt(timerStartedAt);
        ensembleTimer.tick(timerStartedAt.plus(Duration.ofMinutes(4).plusMillis(1)));
        return new Fixture(ensembleTimer, timerStartedAt);
    }

    public static EnsembleTimer createTimerWith4MinuteDuration() {
        return createTimerWith4MinuteDurationAndIdOf(IRRELEVANT_ENSEMBLE_ID);
    }

    public static EnsembleTimer createTimerWith4MinuteDurationAndIdOf(EnsembleId ensembleId) {
        Member member1 = MemberFactory.createMember(1, "One", IRRELEVANT_GITHUB_USERNAME);
        Member member2 = MemberFactory.createMember(2, "Two", IRRELEVANT_GITHUB_USERNAME);
        Member member3 = MemberFactory.createMember(3, "Three", IRRELEVANT_GITHUB_USERNAME);
        Member member4 = MemberFactory.createMember(4, "Four", IRRELEVANT_GITHUB_USERNAME);
        Member member5 = MemberFactory.createMember(5, "Five", IRRELEVANT_GITHUB_USERNAME);

        return new EnsembleTimer(ensembleId,
                                 IRRELEVANT_NAME,
                                 List.of(member1, member2, member3, member4, member5),
                                 Duration.ofMinutes(4));
    }

    public static EnsembleTimer createTimer() {
        Member member1 = MemberFactory.createMember(1, "One", IRRELEVANT_GITHUB_USERNAME);
        Member member2 = MemberFactory.createMember(2, "Two", IRRELEVANT_GITHUB_USERNAME);
        Member member3 = MemberFactory.createMember(3, "Three", IRRELEVANT_GITHUB_USERNAME);

        return new EnsembleTimer(IRRELEVANT_ENSEMBLE_ID,
                                 IRRELEVANT_NAME,
                                 List.of(member1, member2, member3));
    }

    public static void pushTimerToFinishedState(EnsembleTimer ensembleTimer) {
        Instant timerStartedAt = Instant.now();
        ensembleTimer.startTimerAt(timerStartedAt);
        Instant timerFinishedAt = timerStartedAt.plus(EnsembleTimer.DEFAULT_TIMER_DURATION);
        ensembleTimer.tick(timerFinishedAt);
    }

    public record Fixture(EnsembleTimer ensembleTimer, Instant timerStartedAt) {
    }
}
