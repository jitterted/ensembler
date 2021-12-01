package com.jitterted.mobreg.adapter.in.web.admin;

import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class ScheduleEnsembleFormTest {

    @Test
    public void formConvertsDateTimeBasedOnGivenTimezone() throws Exception {
        ScheduleEnsembleForm form = new ScheduleEnsembleForm("test", "", "2021-09-13", "20:00", "America/Los_Angeles");

        ZonedDateTime dateTimeInUtc = form.getDateTimeInUtc();

        assertThat(dateTimeInUtc.getZone())
                .isEqualTo(ZoneOffset.UTC);
        assertThat(dateTimeInUtc.getHour())
                .isEqualTo(20 + 7 - 24);
    }
}