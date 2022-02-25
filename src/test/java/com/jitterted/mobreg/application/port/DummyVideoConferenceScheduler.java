package com.jitterted.mobreg.application.port;

import com.jitterted.mobreg.domain.ConferenceDetails;
import com.jitterted.mobreg.domain.Ensemble;

public class DummyVideoConferenceScheduler implements VideoConferenceScheduler {
    @Override
    public ConferenceDetails createMeeting(Ensemble ensemble) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteMeeting(ConferenceDetails conferenceDetails) {
        return false;
    }
}
