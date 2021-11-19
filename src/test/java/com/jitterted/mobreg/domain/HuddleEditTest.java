package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleEditTest {

    @Test
    public void canChangeNameForExistingHuddle() throws Exception {
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

    @Test
    public void canChangeStartDateTime() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        Huddle huddle = new Huddle("before", now);

        huddle.changeStartDateTimeTo(now.plusDays(7));

        assertThat(huddle.startDateTime())
                .isEqualTo(now.plusDays(7));
    }

    @Test
    public void changeDateTimeToNullNotAllowed() throws Exception {
        Huddle huddle = new Huddle("before", ZonedDateTime.now());

        assertThatThrownBy(() -> {
            huddle.changeStartDateTimeTo(null);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void canChangeToExistingDateTime() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        Huddle huddle = new Huddle("before", now);

        huddle.changeStartDateTimeTo(now.plusSeconds(0)); // want a different object here, but same date/time

        assertThat(huddle.startDateTime())
                .isEqualTo(now);
    }

    @Test
    public void canChangeDateTimeAfterCompleted() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        Huddle huddle = new Huddle("before", now);
        huddle.complete();

        huddle.changeStartDateTimeTo(now.plusHours(1));

        assertThat(huddle.startDateTime())
                .isEqualTo(now.plusHours(1));
    }
}