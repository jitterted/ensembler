package com.jitterted.mobreg.domain;

import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;

public class HuddleFactory {
    @NotNull
    public static Huddle createDefaultHuddleStartTimeNow() {
        return new Huddle("huddle", ZonedDateTime.now());
    }

    @NotNull
    public static Huddle createDefaultHuddleWithIdOf1() {
        Huddle huddle = new Huddle("test", ZonedDateTime.now());
        huddle.setId(HuddleId.of(1L));
        return huddle;
    }
}
