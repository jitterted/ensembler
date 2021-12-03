package com.jitterted.mobreg.application.port;

public class FailedToScheduleMeeting extends RuntimeException {
    public FailedToScheduleMeeting() {
        super();
    }

    public FailedToScheduleMeeting(String message) {
        super(message);
    }
}
