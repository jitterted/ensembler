package com.jitterted.mobreg.adapter;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
    public void givenDateTimeInUtcFormattedAsIso8601WithSuffixOfZ() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2021, 4, 30, 9, 0, 0, 0);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneOffset.UTC);

        String formattedDateTime = DateTimeFormatting.formatAsDateTimeForJavaScriptDateIso8601(zonedDateTime);

        assertThat(formattedDateTime)
                .isEqualTo("2021-04-30T09:00:00Z");
    }

    @Test
    public void dateTimeDuringStandardTimeConvertedToUtcCorrectly() throws Exception {
        int hourInLocalTime = 11;
        LocalDateTime dateTimeDuringStandardTime = LocalDateTime.of(2021, 11, 8, hourInLocalTime, 0, 0, 0);
        ZonedDateTime zonedDateTimeDuringStandardTime =
                dateTimeDuringStandardTime.atZone(ZoneId.of("America/Los_Angeles"));

        ZonedDateTime utcDateTime = zonedDateTimeDuringStandardTime.withZoneSameInstant(ZoneOffset.UTC);

        int hoursPacificTimeZoneIsBehindUtcDuringStandardTime = 8;
        assertThat(utcDateTime)
                .isEqualTo(ZonedDateTime.of(2021, 11, 8, hourInLocalTime + hoursPacificTimeZoneIsBehindUtcDuringStandardTime, 0, 0, 0, ZoneOffset.UTC));
    }

    @Test
    public void dateTimeDuringDaylightSavingsConvertedToUtcCorrectly() throws Exception {
        int hourInLocalTime = 9;
        LocalDateTime dateTimeDuringDaylightSavingsTime = LocalDateTime.of(2021, 11, 1, hourInLocalTime, 0, 0, 0);
        ZonedDateTime zonedDateTimeDuringDaylightSavingsTime =
                dateTimeDuringDaylightSavingsTime.atZone(ZoneId.of("America/Los_Angeles"));

        ZonedDateTime utcDateTime = zonedDateTimeDuringDaylightSavingsTime.withZoneSameInstant(ZoneOffset.UTC);

        int hoursPacificTimeZoneIsBehindUtcDuringDaylightSavingsTime = 7;
        assertThat(utcDateTime)
                .isEqualTo(ZonedDateTime.of(2021, 11, 1, hourInLocalTime + hoursPacificTimeZoneIsBehindUtcDuringDaylightSavingsTime, 0, 0, 0, ZoneOffset.UTC));
    }

}