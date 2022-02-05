package com.jitterted.mobreg.domain;

import java.net.URI;

// Value Object
public record ConferenceDetails(String meetingId, URI startUrl, URI joinUrl) {
    public static final ConferenceDetails EMPTY =
            new ConferenceDetails("", URI.create(""), URI.create(""));
}
