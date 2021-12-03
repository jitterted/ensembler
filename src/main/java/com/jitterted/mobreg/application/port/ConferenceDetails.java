package com.jitterted.mobreg.application.port;

import java.net.URI;

// Value Object
public record ConferenceDetails(String meetingId, URI startUrl, URI joinUrl) {
}
