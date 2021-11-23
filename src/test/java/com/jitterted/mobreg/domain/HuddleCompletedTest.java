package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleCompletedTest {

    @Test
    public void newHuddleIsNotCompleted() throws Exception {
        Huddle huddle = new Huddle("not completed", ZonedDateTime.now());

        assertThat(huddle.isCompleted())
                .isFalse();
    }

    @Test
    public void whenCompletingHuddleThenHuddleIsCompleted() throws Exception {
        Huddle huddle = new Huddle("completed", ZonedDateTime.now());

        huddle.complete();

        assertThat(huddle.isCompleted())
                .isTrue();
    }

    @Test
    public void completedHuddleRegisterMemberThrowsException() throws Exception {
        Huddle huddle = new Huddle("completed", ZonedDateTime.now());
        huddle.complete();

        assertThatThrownBy(() -> huddle.acceptedBy(null))
          .isInstanceOf(HuddleAlreadyCompletedException.class);
    }

    @Test
    public void completeIsIdempotent() throws Exception {
        Huddle huddle = new Huddle("completed", ZonedDateTime.now());
        huddle.complete();

        huddle.complete();

        assertThat(huddle.isCompleted())
                .isTrue();
    }
}