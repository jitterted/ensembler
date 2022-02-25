package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.VideoConferenceScheduler;
import com.jitterted.mobreg.domain.ConferenceDetails;
import com.jitterted.mobreg.domain.Ensemble;

class ZoomConferenceSchedulerDeletesExpectedMeetingId implements VideoConferenceScheduler {
    private final String expectedMeetingId;

    public ZoomConferenceSchedulerDeletesExpectedMeetingId(String expectedMeetingId) {
        this.expectedMeetingId = expectedMeetingId;
    }

    @Override
    public ConferenceDetails createMeeting(Ensemble ensemble) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteMeeting(ConferenceDetails conferenceDetails) {
        return conferenceDetails.meetingId().equals(expectedMeetingId);
    }
}
