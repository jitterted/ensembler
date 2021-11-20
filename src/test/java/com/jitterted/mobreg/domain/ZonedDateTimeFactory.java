package com.jitterted.mobreg.domain;

import org.jetbrains.annotations.NotNull;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ZonedDateTimeFactory {
    @NotNull
    public static ZonedDateTime zoneDateTimeUtc(int year, int month, int dayOfMonth, int hour) {
        return ZonedDateTime.of(year, month, dayOfMonth, hour, 0, 0, 0, ZoneOffset.UTC);
    }
}
