package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.application.EnsembleBuilderAndSaviour;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleStatusTest {

    private static final MemberId DUMMY_MEMBER_ID = MemberId.of(-1);

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
    public void whenCancelingEnsembleThenEnsembleIsCanceled() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();

        ensemble.cancel();

        assertThat(ensemble.isCanceled())
                .isTrue();
    }

    @Test
    public void canceledEnsembleWhenAcceptMemberThrowsException() throws Exception {
        Ensemble ensemble = new EnsembleBuilderAndSaviour().named("canceled").id(-2).build();
        ensemble.cancel();

        assertThatThrownBy(() -> {
            ensemble.acceptedBy(DUMMY_MEMBER_ID);
        })
                .isInstanceOf(EnsembleCanceled.class)
                .hasMessage("Ensemble (EnsembleId=-2) is Canceled: cannot accept member (MemberId=-1)");
    }

    @Test
    public void completedEnsembleWhenRegisterMemberThrowsException() throws Exception {
        Ensemble ensemble = new Ensemble("completed", ZonedDateTime.now());
        ensemble.complete();

        assertThatThrownBy(() -> ensemble.acceptedBy(DUMMY_MEMBER_ID))
                .isInstanceOf(EnsembleCompletedException.class);
    }

    @Test
    public void completeIsIdempotent() throws Exception {
        Ensemble ensemble = new Ensemble("completed", ZonedDateTime.now());
        ensemble.complete();

        ensemble.complete();

        assertThat(ensemble.isCompleted())
                .isTrue();
    }
}