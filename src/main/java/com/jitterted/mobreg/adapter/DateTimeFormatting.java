package com.jitterted.mobreg.adapter;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatting {

    // Date format for browsers <input type="date"> tag is YYYY-MM-DD -- dashes only! (not slash separators)
    // Time format for the browser's <input type="time"> tag is HH:MM in 24 hour format
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final ZoneId PACIFIC_TIME_ZONE_ID = ZoneId.of("America/Los_Angeles");
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

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

    @NotNull
    public static String extractFormattedTimeFrom(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(HH_MM);
    }

    @NotNull
    public static String extractFormattedDateFrom(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(YYYY_MM_DD);
    }
}
