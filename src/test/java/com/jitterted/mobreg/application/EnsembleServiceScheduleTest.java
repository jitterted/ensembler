package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.VideoConferenceScheduler;
import com.jitterted.mobreg.domain.ConferenceDetails;
import com.jitterted.mobreg.domain.Ensemble;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleServiceScheduleTest {

    @Test
    void singleScheduledEnsembleIsReturnedForAllEnsembles() throws Exception {
        EnsembleService ensembleService = EnsembleServiceFactory.withDefaults();

        ensembleService.scheduleEnsemble("Name", ZonedDateTime.now());

        assertThat(ensembleService.allEnsembles())
                .hasSize(1);
    }

    @Test
    void ensembleScheduledWithManuallyEnteredZoomLinkThenHasZoomLink() throws Exception {
        EnsembleService ensembleService = EnsembleServiceFactory.withDefaults();

        ensembleService.scheduleEnsemble("With Zoom", URI.create("https://zoom.us/j/123456?pwd=12345"), ZonedDateTime.now());

        assertThat(ensembleService.allEnsembles())
                .extracting(Ensemble::meetingLink)
                .extracting(URI::toString)
                .containsOnly("https://zoom.us/j/123456?pwd=12345");
    }

    @Test
    void ensembleScheduledThenZoomLinkFetchedFromApiHasConferenceDetails() throws Exception {
        ConferenceDetails expectedConferenceDetails = new ConferenceDetails("123",
                                                                            URI.create("https://zoom.us/startUrl"),
                                                                            URI.create("https://zoom.us/joinUrl"));
        VideoConferenceScheduler stubScheduler = new StubConferenceScheduler(expectedConferenceDetails);
        EnsembleService ensembleService = EnsembleServiceFactory.with(stubScheduler);

        ensembleService.scheduleEnsembleWithVideoConference("With Zoom", ZonedDateTime.now());

        assertThat(ensembleService.allEnsembles())
                .extracting(Ensemble::conferenceDetails)
                .containsOnly(expectedConferenceDetails);
    }

    @Test
    void apiFailedToReturnValidConferenceDetailsThenConferenceDetailsIsDefaultUnscheduled() throws Exception {
        EnsembleService ensembleService = EnsembleServiceFactory.with(new FailsToCreateMeetingConferenceScheduler());

        ensembleService.scheduleEnsembleWithVideoConference("With Zoom", ZonedDateTime.now());

        ConferenceDetails originalConferenceDetails = new ConferenceDetails("", URI.create(""), URI.create("https://zoom.us"));
        assertThat(ensembleService.allEnsembles())
                .extracting(Ensemble::conferenceDetails)
                .containsOnly(originalConferenceDetails);
    }

    private static class StubConferenceScheduler implements VideoConferenceScheduler {
        private final ConferenceDetails stubConferenceDetails;

        public StubConferenceScheduler(ConferenceDetails stubConferenceDetails) {
            this.stubConferenceDetails = stubConferenceDetails;
        }

        @Override
        public ConferenceDetails createMeeting(Ensemble ensemble) {
            return stubConferenceDetails;
        }

        @Override
        public boolean deleteMeeting(ConferenceDetails conferenceDetails) {
            throw new UnsupportedOperationException();
        }
    }

}