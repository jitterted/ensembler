package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.DummyNotifier;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.VideoConferenceScheduler;
import com.jitterted.mobreg.domain.ConferenceDetails;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.ZonedDateTimeFactory;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleServiceEditExistingTest {

    @Test
    void changesToNameAndDateTimeForExistingEnsembleAreSaved() throws Exception {
        InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository);
        Ensemble ensemble = new Ensemble("Before", URI.create("https://zoom.us/before"),
                                         ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 19, 17));
        EnsembleId ensembleId = ensembleRepository.save(ensemble).getId();

        ZonedDateTime afterZonedDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 20, 18);
        ensembleService.changeTo(ensembleId, "After", "https://zoom.us/before", afterZonedDateTime);

        Ensemble found = ensembleRepository.findById(ensembleId).orElseThrow();
        assertThat(found.name())
                .isEqualTo("After");
        assertThat(found.startDateTime())
                .isEqualTo(afterZonedDateTime);
    }

    @Test
    void changingZoomLinkToBeBlankThenFetchesNewLinkViaSchedulerApi() throws Exception {
        InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        final URI newZoomMeetingLink = URI.create("https://us06web.zoom.us/j/83912958607?pwd=U0hNbk84a2IraEMrWi9WZ2xYalFCZz09");
        VideoConferenceScheduler videoConferenceScheduler = new StubConferenceScheduler(newZoomMeetingLink);
        EnsembleService ensembleService = new EnsembleService(ensembleRepository, new InMemoryMemberRepository(),
                                                              new DummyNotifier(), videoConferenceScheduler);
        ZonedDateTime startDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 12, 17, 17);
        Ensemble ensemble = new Ensemble("Before", URI.create("https://zoom.us/"),
                                         startDateTime);
        EnsembleId ensembleId = ensembleRepository.save(ensemble).getId();

        ensembleService.changeTo(ensembleId, "Before", "", startDateTime);

        Ensemble updatedEnsemble = ensembleService.findById(ensembleId).orElseThrow();
        assertThat(updatedEnsemble.meetingLink())
                .isEqualTo(newZoomMeetingLink);
    }

    @Test
    void changingZoomLinkWithValidLinkThenStoresNewLinkDoesNotFetchFromScheduler() throws Exception {
        InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository);
        ZonedDateTime startDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 19, 17);
        String name = "Before";
        Ensemble ensemble = new Ensemble(name, URI.create("https://zoom.us/before"), startDateTime);
        EnsembleId ensembleId = ensembleRepository.save(ensemble).getId();

        ensembleService.changeTo(ensembleId, name, "https://new.zoom.link", startDateTime);

        Ensemble updatedEnsemble = ensembleService.findById(ensembleId).orElseThrow();
        assertThat(updatedEnsemble.meetingLink())
                .isEqualTo(URI.create("https://new.zoom.link"));
    }

    private static class StubConferenceScheduler implements VideoConferenceScheduler {
        private final URI newZoomMeetingLink;

        public StubConferenceScheduler(URI newZoomMeetingLink) {
            this.newZoomMeetingLink = newZoomMeetingLink;
        }

        @Override
        public ConferenceDetails createMeeting(Ensemble ensemble) {
            return new ConferenceDetails("meetingId",
                                         URI.create("https://zoom/us/start"),
                                         newZoomMeetingLink);
        }

        @Override
        public boolean deleteMeeting(ConferenceDetails conferenceDetails) {
            throw new UnsupportedOperationException();
        }
    }
}