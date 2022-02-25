package com.jitterted.mobreg.domain;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.Objects;

// Value Object
public record ConferenceDetails(@NotNull String meetingId, @NotNull URI startUrl, @NotNull URI joinUrl) {

    public ConferenceDetails {
        Objects.requireNonNull(meetingId);
        Objects.requireNonNull(startUrl);
        Objects.requireNonNull(joinUrl);
    }

    public static final ConferenceDetails DELETED =
            new ConferenceDetails("deleted", URI.create("https://deleted.link"), URI.create("https://deleted.link"));

    public boolean hasValidMeetingId() {
        return !meetingId.isBlank();
    }
}
