package com.jitterted.mobreg.domain;

import java.net.URI;

public class ZoomConferenceDetails {
    private URI zoomMeetingLink;

    public URI joinUrl() {
        return zoomMeetingLink;
    }

    public void setZoomMeetingLink(URI zoomMeetingLink) {
        this.zoomMeetingLink = zoomMeetingLink;
    }
}