package com.jitterted.mobreg.application.port;

import com.jitterted.mobreg.domain.Ensemble;

public class DummyVideoConferenceScheduler implements VideoConferenceScheduler {
    @Override
    public ConferenceDetails createMeeting(Ensemble ensemble) {
        throw new UnsupportedOperationException();
    }
}
