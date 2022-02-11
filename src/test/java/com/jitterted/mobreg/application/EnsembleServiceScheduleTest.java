package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.FailedToScheduleMeeting;
import com.jitterted.mobreg.application.port.VideoConferenceScheduler;
import com.jitterted.mobreg.domain.ConferenceDetails;
import com.jitterted.mobreg.domain.Ensemble;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleServiceScheduleTest {

    @Test
    public void singleScheduledEnsembleIsReturnedForAllEnsembles() throws Exception {
        EnsembleService ensembleService = EnsembleServiceFactory.withDefaults();

        ensembleService.scheduleEnsemble("Name", ZonedDateTime.now());

        assertThat(ensembleService.allEnsembles())
                .hasSize(1);
    }

    @Test
    public void ensembleScheduledWithManuallyEnteredZoomLinkThenHasZoomLink() throws Exception {
        EnsembleService ensembleService = EnsembleServiceFactory.withDefaults();

        ensembleService.scheduleEnsemble("With Zoom", URI.create("https://zoom.us/j/123456?pwd=12345"), ZonedDateTime.now());

        assertThat(ensembleService.allEnsembles())
                .extracting(Ensemble::zoomMeetingLink)
                .extracting(URI::toString)
                .containsOnly("https://zoom.us/j/123456?pwd=12345");
    }

    @Test
    public void ensembleScheduledThenZoomLinkFetchedFromApiHasLink() throws Exception {
        VideoConferenceScheduler stubScheduler = new StubConferenceScheduler();
        EnsembleService ensembleService = EnsembleServiceFactory.with(stubScheduler);

        ensembleService.scheduleEnsembleWithVideoConference("With Zoom", ZonedDateTime.now());

        assertThat(ensembleService.allEnsembles())
                .extracting(Ensemble::zoomMeetingLink)
                .extracting(URI::toString)
                .containsOnly("https://zoom.us/joinUrl");
    }

    @Test
    public void apiFailedToReturnValidConferenceDetailsThenMeetingLinkIsBlank() throws Exception {
        VideoConferenceScheduler stubScheduler = ensemble -> {
            throw new FailedToScheduleMeeting("Force exception within test");
        };
        EnsembleService ensembleService = EnsembleServiceFactory.with(stubScheduler);

        ensembleService.scheduleEnsembleWithVideoConference("With Zoom", ZonedDateTime.now());

        assertThat(ensembleService.allEnsembles())
                .extracting(Ensemble::zoomMeetingLink)
                .extracting(URI::toString)
                .containsOnly("https://zoom.us"); // default meeting link
    }

    private static class StubConferenceScheduler implements VideoConferenceScheduler {
        @Override
        public ConferenceDetails createMeeting(Ensemble ensemble) {
            return new ConferenceDetails("123",
                                         URI.create("https://zoom.us/startUrl"),
                                         URI.create("https://zoom.us/joinUrl"));
        }
    }
}