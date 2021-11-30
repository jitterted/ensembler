package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleEditTest {

    @Test
    public void canChangeNameForExistingEnsemble() throws Exception {
        Ensemble before = new Ensemble("before", ZonedDateTime.now());

        before.changeNameTo("after");

        assertThat(before.name())
                .isEqualTo("after");
    }

    @Test
    public void changeNameToNullNotAllowed() throws Exception {
        Ensemble ensemble = new Ensemble("before", ZonedDateTime.now());

        assertThatThrownBy(() -> {
            ensemble.changeNameTo(null);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void canChangeToExistingName() throws Exception {
        Ensemble before = new Ensemble("before", ZonedDateTime.now());

        before.changeNameTo("before");

        assertThat(before.name())
                .isEqualTo("before");
    }

    @Test
    public void canChangeNameAfterCompleted() throws Exception {
        Ensemble ensemble = new Ensemble("before", ZonedDateTime.now());
        ensemble.complete();

        ensemble.changeNameTo("after");

        assertThat(ensemble.name())
                .isEqualTo("after");
    }

    @Test
    public void canChangeStartDateTime() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        Ensemble ensemble = new Ensemble("before", now);

        ensemble.changeStartDateTimeTo(now.plusDays(7));

        assertThat(ensemble.startDateTime())
                .isEqualTo(now.plusDays(7));
    }

    @Test
    public void changeDateTimeToNullNotAllowed() throws Exception {
        Ensemble ensemble = new Ensemble("before", ZonedDateTime.now());

        assertThatThrownBy(() -> {
            ensemble.changeStartDateTimeTo(null);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void canChangeToExistingDateTime() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        Ensemble ensemble = new Ensemble("before", now);

        ensemble.changeStartDateTimeTo(now.plusSeconds(0)); // want a different object here, but same date/time

        assertThat(ensemble.startDateTime())
                .isEqualTo(now);
    }

    @Test
    public void canChangeDateTimeAfterCompleted() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        Ensemble ensemble = new Ensemble("before", now);
        ensemble.complete();

        ensemble.changeStartDateTimeTo(now.plusHours(1));

        assertThat(ensemble.startDateTime())
                .isEqualTo(now.plusHours(1));
    }
}