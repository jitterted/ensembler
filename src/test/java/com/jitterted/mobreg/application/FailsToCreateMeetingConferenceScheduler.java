package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.FailedToScheduleMeeting;
import com.jitterted.mobreg.application.port.VideoConferenceScheduler;
import com.jitterted.mobreg.domain.ConferenceDetails;
import com.jitterted.mobreg.domain.Ensemble;

class FailsToCreateMeetingConferenceScheduler implements VideoConferenceScheduler {
    @Override
    public ConferenceDetails createMeeting(Ensemble ensemble) {
        throw new FailedToScheduleMeeting("Force exception within test");
    }

    @Override
    public boolean deleteMeeting(ConferenceDetails conferenceDetails) {
        throw new UnsupportedOperationException();
    }
}
