package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.application.MemberFactory;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;

public class HuddleFactory {
    @NotNull
    public static Ensemble createDefaultHuddleStartTimeNow() {
        return new Ensemble("huddle", ZonedDateTime.now());
    }

    @NotNull
    public static Ensemble withStartTime(int year, int month, int dayOfMonth, int hour) {
        return new Ensemble("huddle", ZonedDateTimeFactory.zoneDateTimeUtc(year, month, dayOfMonth, hour));
    }

    @NotNull
    public static Ensemble createDefaultHuddleWithIdOf1() {
        Ensemble ensemble = new Ensemble("test", ZonedDateTime.now());
        ensemble.setId(EnsembleId.of(1L));
        return ensemble;
    }

    @NotNull
    public static Ensemble createHuddleWithIdOf1AndOneDayInTheFuture() {
        Ensemble ensemble = new Ensemble("test", ZonedDateTime.now().plusDays(1));
        ensemble.setId(EnsembleId.of(1L));
        return ensemble;
    }

    @NotNull
    public static Ensemble fullHuddleWithStartTime(int year, int month, int dayOfMonth, int hour) {
        Ensemble futureEnsemble = withStartTime(year, month, dayOfMonth, hour);
        MemberFactory.registerCountMembersWithHuddle(futureEnsemble, 5);
        return futureEnsemble;
    }
}
