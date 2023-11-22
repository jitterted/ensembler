package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleFactory;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class ScheduleEnsembleFormTest {

    @Test
    void formConvertsDateTimeBasedOnGivenTimezone() throws Exception {
        ScheduleEnsembleForm form = new ScheduleEnsembleForm("test", "", "2021-09-13", "20:00", "America/Los_Angeles");

        ZonedDateTime dateTimeInUtc = form.getDateTimeInUtc();

        assertThat(dateTimeInUtc.getZone())
                .isEqualTo(ZoneOffset.UTC);
        assertThat(dateTimeInUtc.getHour())
                .isEqualTo(20 + 7 - 24);
    }

    @Test
    void ensembleDateTimeConvertedToFormBasedDateAndTimeFields() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTime(2021, 12, 15, 21);

        // Assumes this form is always in America/Los_Angeles
        // otherwise we'll need to pull the time zone from the member's profile
        ScheduleEnsembleForm scheduleEnsembleForm = ScheduleEnsembleForm.from(ensemble);

        assertThat(scheduleEnsembleForm.getDate())
                .isEqualTo("2021-12-15");
        assertThat(scheduleEnsembleForm.getTime())
                .isEqualTo("13:00");
        assertThat(scheduleEnsembleForm.getTimezone())
                .isEqualTo("America/Los_Angeles");
    }
}