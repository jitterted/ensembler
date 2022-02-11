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
                .completed()
                .build();

        assertThatThrownBy(ensemble::cancel)
                .isInstanceOf(EnsembleCompleted.class);
    }

    @Test
    public void canceledEnsembleWhenAcceptMemberThrowsException() throws Exception {
        Ensemble ensemble = new EnsembleBuilderAndSaviour()
                .id(-2)
                .named("canceled")
                .cancel()
                .build();

        assertThatThrownBy(() -> {
            ensemble.acceptedBy(DUMMY_MEMBER_ID);
        })
                .isInstanceOf(EnsembleCanceled.class)
                .hasMessage("Ensemble (EnsembleId=-2) is Canceled");
    }

    @Test
    public void completedEnsembleWhenRegisterMemberThrowsException() throws Exception {
        Ensemble ensemble = new EnsembleBuilderAndSaviour().named("completed").id(-2).build();
        ensemble.complete();

        assertThatThrownBy(() -> ensemble.acceptedBy(DUMMY_MEMBER_ID))
                .isInstanceOf(EnsembleCompleted.class)
                .hasMessage("Ensemble (EnsembleId=-2) is Completed");
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