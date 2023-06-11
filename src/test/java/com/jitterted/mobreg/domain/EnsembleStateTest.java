package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleStateTest {

    private static final MemberId DUMMY_MEMBER_ID = MemberId.of(-1);
    private static final Duration ENSEMBLE_DURATION = Duration.ofHours(1).plusMinutes(55);

    @Test
    public void newEnsembleIsNotCanceled() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();

        assertThat(ensemble.isCanceled())
                .isFalse();
    }

    @Test
    public void newEnsembleIsNotCompleted() throws Exception {
        Ensemble ensemble = new Ensemble("not completed", ZonedDateTime.now());

        assertThat(ensemble.isCompleted())
                .isFalse();
    }

    @Test
    public void whenCompletingEnsembleThenEnsembleIsCompleted() throws Exception {
        Ensemble ensemble = new Ensemble("completed", ZonedDateTime.now());

        ensemble.complete();

        assertThat(ensemble.isCompleted())
                .isTrue();
    }

    @Test
    public void whenCancelingScheduledEnsembleThenEnsembleIsCanceled() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();

        ensemble.cancel();

        assertThat(ensemble.isCanceled())
                .isTrue();
    }

    @Test
    public void cancelCompletedEnsembleThenThrowsException() throws Exception {
        Ensemble ensemble = new EnsembleBuilderAndSaviour()
                .id(-2)
                .named("completed")
                .asCompleted()
                .build();

        assertThatThrownBy(ensemble::cancel)
                .isInstanceOf(EnsembleCompleted.class);
    }

    @Test
    public void canceledEnsembleWhenAcceptMemberThrowsException() throws Exception {
        Ensemble ensemble = new EnsembleBuilderAndSaviour()
                .id(-2)
                .named("canceled")
                .asCanceled()
                .build();

        assertThatThrownBy(() -> {
            ensemble.acceptedBy(DUMMY_MEMBER_ID);
        })
                .isInstanceOf(EnsembleCanceled.class)
                .hasMessage("Ensemble (EnsembleId=-2) is Canceled");
    }

    @Test
    public void completedEnsembleWhenRegisterMemberThrowsException() throws Exception {
        Ensemble ensemble = new EnsembleBuilderAndSaviour()
                .id(-2)
                .named("completed")
                .asCompleted()
                .build();

        assertThatThrownBy(() -> ensemble.acceptedBy(DUMMY_MEMBER_ID))
                .isInstanceOf(EnsembleCompleted.class)
                .hasMessage("Ensemble (EnsembleId=-2) is Completed");
    }

    @Test
    public void completingCanceledEnsembleThrowsException() throws Exception {
        Ensemble ensemble = new EnsembleBuilderAndSaviour()
                .id(-2)
                .named("canceled")
                .asCanceled()
                .build();

        assertThatThrownBy(ensemble::complete)
                .isInstanceOf(EnsembleCanceled.class)
                .hasMessage("Ensemble (EnsembleId=-2) is Canceled");
    }

    @Test
    public void completeIsIdempotent() throws Exception {
        Ensemble ensemble = new Ensemble("completed", ZonedDateTime.now());
        ensemble.complete();

        ensemble.complete();

        assertThat(ensemble.isCompleted())
                .isTrue();
    }


    @Test
    public void uncompletedEnsembleHasNotEndedThenPendingCompletedIsFalse() throws Exception {
        ZonedDateTime startTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 12);
        Ensemble ensemble = EnsembleFactory.withStartTime(startTime);

        assertThat(ensemble.isPendingCompletedAsOf(
                startTime.plus(ENSEMBLE_DURATION.minusMinutes(1))))
                .isFalse();
    }

    @Test
    public void uncompletedEnsembleEndedInThePastThenPendingCompletedIsTrue() throws Exception {
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
}