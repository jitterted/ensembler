package com.jitterted.mobreg.domain;

import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;

public class EnsembleFactory {
    @NotNull
    public static Ensemble withStartTimeNow() {
        return new Ensemble("ensemble", ZonedDateTime.now());
    }

    @NotNull
    public static Ensemble withStartTime(int year, int month, int dayOfMonth, int hour) {
        return new Ensemble("ensemble", ZonedDateTimeFactory.zoneDateTimeUtc(year, month, dayOfMonth, hour));
    }

    @NotNull
    public static Ensemble withIdOf1AndOneDayInTheFuture() {
        Ensemble ensemble = new Ensemble("test", ZonedDateTime.now().plusDays(1));
        ensemble.setId(EnsembleId.of(1L));
        return ensemble;
    }

    @NotNull
    public static Ensemble oneDayInTheFuture() {
        Ensemble ensemble = new Ensemble("test", ZonedDateTime.now().plusDays(1));
        ensemble.setId(EnsembleId.of(1L));
        return ensemble;
    }

    @NotNull
    public static Ensemble ensembleAtCapacityWithStartTime(int year, int month, int dayOfMonth, int hour) {
        Ensemble futureEnsemble = withStartTime(year, month, dayOfMonth, hour);
        acceptCountMembersFor(5, futureEnsemble);
        return futureEnsemble;
    }

    public static Ensemble withStartTime(ZonedDateTime startDateTime) {
        return new Ensemble("Ensemble Factory", startDateTime);
    }

    public static void acceptCountMembersFor(int count, Ensemble ensemble) {
        for (int i = 0; i < count; i++) {
            ensemble.joinAsParticipant(MemberId.of(i));
        }
    }

    public static Ensemble fullEnsembleOneDayInTheFuture() {
        Ensemble ensemble = withIdOf1AndOneDayInTheFuture();
        acceptCountMembersFor(5, ensemble);
        return ensemble;
    }

    @NotNull
    public static Ensemble oneDayInThePast() {
        Ensemble ensemble = withStartTime(ZonedDateTime.now().minusDays(1));
        ensemble.setId(EnsembleId.of(11));
        return ensemble;
    }
}
