package com.jitterted.moborg.adapter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatting {

    // Date format for browsers <input type="date"> tag is YYYY-MM-DD -- dashes only! (not slash separators)
    // Time format for the browser's <input type="time"> tag is HH:MM in 24 hour format
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static ZonedDateTime fromBrowserDateAndTime(String rawDate, String rawTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(rawDate + " " + rawTime, YYYY_MM_DD_HH_MM_FORMATTER);
        return ZonedDateTime.of(localDateTime, ZoneId.of("America/Los_Angeles"));
    }

    public static String formatAsDateTime(ZonedDateTime zonedDateTime) {
        return DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm a").format(zonedDateTime);
    }
}
