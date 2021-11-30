package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleCompletedTest {

    @Test
    public void newHuddleIsNotCompleted() throws Exception {
        Ensemble ensemble = new Ensemble("not completed", ZonedDateTime.now());

        assertThat(ensemble.isCompleted())
                .isFalse();
    }

    @Test
    public void whenCompletingHuddleThenHuddleIsCompleted() throws Exception {
        Ensemble ensemble = new Ensemble("completed", ZonedDateTime.now());

        ensemble.complete();

        assertThat(ensemble.isCompleted())
                .isTrue();
    }

    @Test
    public void completedHuddleRegisterMemberThrowsException() throws Exception {
        Ensemble ensemble = new Ensemble("completed", ZonedDateTime.now());
        ensemble.complete();

        assertThatThrownBy(() -> ensemble.acceptedBy(null))
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