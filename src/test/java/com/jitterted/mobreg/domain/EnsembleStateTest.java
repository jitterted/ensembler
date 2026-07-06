package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnsembleStateTest {

    private static final MemberId DUMMY_MEMBER_ID = MemberId.of(-1);
    private static final Duration ENSEMBLE_DURATION = Duration.ofHours(1).plusMinutes(55);

    @Test
    void newEnsembleIsNotCanceled() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();

        assertThat(ensemble.isCanceled())
                .isFalse();
    }

    @Test
    void newEnsembleIsNotCompleted() {
        Ensemble ensemble = new Ensemble("not completed", ZonedDateTime.now());

        assertThat(ensemble.isCompleted())
                .isFalse();
    }

    @Test
    void whenCompletingEnsembleThenEnsembleIsCompleted() {
        Ensemble ensemble = new Ensemble("completed", ZonedDateTime.now());

        ensemble.complete();

        assertThat(ensemble.isCompleted())
                .isTrue();
    }

    @Test
    void whenCancelingScheduledEnsembleThenEnsembleIsCanceled() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();

        ensemble.cancel();

        assertThat(ensemble.isCanceled())
                .isTrue();
    }

    @Test
    void cancelCompletedEnsembleThenThrowsException() {
        Ensemble ensemble = new EnsembleBuilder()
                .id(-2)
                .named("completed")
                .asCompleted()
                .build();

        assertThatThrownBy(ensemble::cancel)
                .isInstanceOf(EnsembleCompleted.class);
    }

    @Test
    void canceledEnsembleWhenAcceptMemberThrowsException() {
        Ensemble ensemble = new EnsembleBuilder()
                .id(-2)
                .named("canceled")
                .asCanceled()
                .build();

        assertThatThrownBy(() -> ensemble.joinAsParticipant(DUMMY_MEMBER_ID))
                .isInstanceOf(EnsembleCanceled.class)
                .hasMessage("Ensemble (EnsembleId=-2) is Canceled");
    }

    @Test
    void completedEnsembleWhenRegisterMemberThrowsException() {
        Ensemble ensemble = new EnsembleBuilder()
                .id(-2)
                .named("completed")
                .asCompleted()
                .build();

        assertThatThrownBy(() -> ensemble.joinAsParticipant(DUMMY_MEMBER_ID))
                .isInstanceOf(EnsembleCompleted.class)
                .hasMessage("Ensemble (EnsembleId=-2) is Completed");
    }

    @Test
    void completingCanceledEnsembleThrowsException() {
        Ensemble ensemble = new EnsembleBuilder()
                .id(-2)
                .named("canceled")
                .asCanceled()
                .build();

        assertThatThrownBy(ensemble::complete)
                .isInstanceOf(EnsembleCanceled.class)
                .hasMessage("Ensemble (EnsembleId=-2) is Canceled");
    }

    @Test
    void completeIsIdempotent() {
        Ensemble ensemble = new Ensemble("completed", ZonedDateTime.now());
        ensemble.complete();

        ensemble.complete();

        assertThat(ensemble.isCompleted())
                .isTrue();
    }


    @Test
    void uncompletedEnsembleHasNotEndedThenPendingCompletedIsFalse() {
        ZonedDateTime startTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 12);
        Ensemble ensemble = EnsembleFactory.withStartTime(startTime);

        assertThat(ensemble.isPendingCompletedAsOf(
                startTime.plus(ENSEMBLE_DURATION.minusMinutes(1))))
                .isFalse();
    }

    @Test
    void uncompletedEnsembleEndedInThePastThenPendingCompletedIsTrue() {
        ZonedDateTime startTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 12);
        Ensemble ensemble = EnsembleFactory.withStartTime(startTime);

        assertThat(ensemble.isPendingCompletedAsOf(
                startTime.plus(ENSEMBLE_DURATION.plusMinutes(1))))
                .isTrue();
    }

    @Test
    void completedEnsembleEndedInThePastThenPendingCompletedIsFalse() {
        ZonedDateTime startTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 12);
        Ensemble ensemble = EnsembleFactory.withStartTime(startTime);

        ensemble.complete();

        assertThat(ensemble.isPendingCompletedAsOf(
                startTime.plus(ENSEMBLE_DURATION.plusMinutes(1))))
                .isFalse();
    }

    @Test
    void canceledEnsembleEndedInThePastThenPendingCompletedIsFalse() {
        ZonedDateTime startTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 12);
        Ensemble ensemble = EnsembleFactory.withStartTime(startTime);

        ensemble.cancel();

        assertThat(ensemble.isPendingCompletedAsOf(
                startTime.plus(ENSEMBLE_DURATION.plusMinutes(1))))
                .isFalse();
    }

    @Test
    void ensembleScheduledFor1HourFromNowIsAvailableForRegistration() {
        ZonedDateTime now = ZonedDateTimeFactory.zoneDateTimeUtc(2024, 1, 11, 8);
        Ensemble ensembleStartsInOneHour = EnsembleFactory.withStartTime(now.plusHours(1));

        assertThat(ensembleStartsInOneHour.isUpcoming(now))
                .isTrue();
    }

    @Test
    void endedEnsembleIsNotAvailableForRegistration() {
        ZonedDateTime now = ZonedDateTimeFactory.zoneDateTimeUtc(2024, 1, 11, 8);
        Ensemble ensembleStartsInOneHour = EnsembleFactory.withStartTime(now.minusHours(2));

        assertThat(ensembleStartsInOneHour.isUpcoming(now))
                .isFalse();
    }

    @Test
    void ensembleScheduledForFutureButCanceledIsNotAvailableForRegistration() {
        ZonedDateTime now = ZonedDateTimeFactory.zoneDateTimeUtc(2024, 1, 11, 8);
        Ensemble ensemble = EnsembleFactory.withStartTime(now.plusHours(1));
        ensemble.cancel();

        assertThat(ensemble.isUpcoming(now))
                .as("Expected to NOT be available for registration")
                .isFalse();
    }

}