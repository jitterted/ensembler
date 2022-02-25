package com.jitterted.mobreg.domain;

import java.net.URI;

// Value Object
public record ConferenceDetails(String meetingId, URI startUrl, URI joinUrl) {
    public static final ConferenceDetails DELETED =
            new ConferenceDetails("deleted", URI.create("https://deleted.link"), URI.create("https://deleted.link"));
}
