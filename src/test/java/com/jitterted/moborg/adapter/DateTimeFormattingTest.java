package com.jitterted.moborg.adapter;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class DateTimeFormattingTest {

    @Test
    public void browserFormattedDateAndTimeAreConvertedToZonedDateTime() throws Exception {
        String rawDate = "2021-04-30";
        String rawTime = "09:00";

        ZonedDateTime zonedDateTime = DateTimeFormatting.fromBrowserDateAndTime(rawDate, rawTime);

        assertThat(zonedDateTime)
                .isEqualTo(ZonedDateTime.of(2021, 4, 30, 9, 0, 0, 0, ZoneId.of("America/Los_Angeles")));
    }

    @Test
    public void givenPdtTimeZoneDateTimeFormattedAsMonthDayYear12HourTimeWithPdtTimeZone() throws Exception {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2021, 4, 30, 9, 0, 0, 0, ZoneId.of("America/Los_Angeles"));

        String formattedDateTime = DateTimeFormatting.formatAsDateTime(zonedDateTime);

        assertThat(formattedDateTime)
                .isEqualTo("04/30/2021 09:00 AM PDT");
    }

    @Test
    public void givenUtcDateTimeFormattedAsMonthDayYear12HourTimeInPdt() throws Exception {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2021, 4, 30, 11, 0, 0, 0, ZoneId.of("Z"));

        String formattedDateTime = DateTimeFormatting.formatAsDateTime(zonedDateTime);

        assertThat(formattedDateTime)
                .isEqualTo("04/30/2021 04:00 AM PDT");
    }

}