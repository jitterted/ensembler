package com.jitterted.mobreg.application.port;

import com.jitterted.mobreg.domain.ConferenceDetails;
import com.jitterted.mobreg.domain.Ensemble;

public interface VideoConferenceScheduler {
    ConferenceDetails createMeeting(Ensemble ensemble);
}
