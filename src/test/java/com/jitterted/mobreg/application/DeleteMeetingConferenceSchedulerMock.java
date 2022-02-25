package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.VideoConferenceScheduler;
import com.jitterted.mobreg.domain.ConferenceDetails;
import com.jitterted.mobreg.domain.Ensemble;

import static org.assertj.core.api.Assertions.*;

class DeleteMeetingConferenceSchedulerMock implements VideoConferenceScheduler {
    private boolean deleteMeetingWasCalled;
    private ConferenceDetails conferenceDetails;

    @Override
    public ConferenceDetails createMeeting(Ensemble ensemble) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteMeeting(ConferenceDetails conferenceDetails) {
        deleteMeetingWasCalled = true;
        this.conferenceDetails = conferenceDetails;
        return false;
    }

    public void verifyDeleteMeetingWasNotCalled() {
        assertThat(deleteMeetingWasCalled)
                .describedAs("Expected deleteMeeting() not to be called, but it was. ConferenceDetails: " + conferenceDetails)
                .isFalse();
    }
}
