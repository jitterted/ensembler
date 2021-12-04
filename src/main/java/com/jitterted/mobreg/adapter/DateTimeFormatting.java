package com.jitterted.mobreg.adapter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatting {

    // Date format for browsers <input type="date"> tag is YYYY-MM-DD -- dashes only! (not slash separators)
    // Time format for the browser's <input type="time"> tag is HH:MM in 24 hour format
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final ZoneId PACIFIC_TIME_ZONE_ID = ZoneId.of("America/Los_Angeles");

    public static ZonedDateTime fromBrowserDateAndTime(String rawDate, String rawTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(rawDate + " " + rawTime, YYYY_MM_DD_HH_MM_FORMATTER);
        return ZonedDateTime.of(localDateTime, PACIFIC_TIME_ZONE_ID);
    }

    /**
     * Format Zoned date time as used by browser's Date parsing function Date.parse()
     * and elsewhere, such as in JSON
     *
     * @param zonedDateTime date time, usually in UTC (time zone "Z")
     * @return String formatted for use in JavaScript
     */
    @NotNull
    public static String formatAsDateTimeForCommonIso8601(ZonedDateTime zonedDateTime) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(zonedDateTime);
    }
}
