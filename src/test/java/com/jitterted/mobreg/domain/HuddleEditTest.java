package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleEditTest {

    @Test
    public void canEditNameForExistingHuddle() throws Exception {
        Huddle before = new Huddle("before", ZonedDateTime.now());

        before.changeNameTo("after");

        assertThat(before.name())
                .isEqualTo("after");
    }

    @Test
    public void changeNameToNullNotAllowed() throws Exception {
        Huddle huddle = new Huddle("before", ZonedDateTime.now());

        assertThatThrownBy(() -> {
            huddle.changeNameTo(null);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void canChangeToExistingName() throws Exception {
        Huddle before = new Huddle("before", ZonedDateTime.now());

        before.changeNameTo("before");

        assertThat(before.name())
                .isEqualTo("before");
    }

    @Test
    public void canChangeNameAfterCompleted() throws Exception {
        Huddle huddle = new Huddle("before", ZonedDateTime.now());
        huddle.complete();

        huddle.changeNameTo("after");

        assertThat(huddle.name())
                .isEqualTo("after");
    }

}