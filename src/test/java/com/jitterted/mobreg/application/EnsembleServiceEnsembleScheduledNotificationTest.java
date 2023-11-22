package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.DummyVideoConferenceScheduler;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("RedundantThrows")
class EnsembleServiceEnsembleScheduledNotificationTest {

    @Test
    void whenEnsembleScheduledEnsembleOpenNotificationIsSent() throws Exception {
        MockEnsembleScheduledNotifier mockEnsembleScheduledNotifier = new MockEnsembleScheduledNotifier();
        EnsembleService ensembleService = new EnsembleService(new InMemoryEnsembleRepository(),
                                                              new InMemoryMemberRepository(),
                                                              mockEnsembleScheduledNotifier, new DummyVideoConferenceScheduler());

        ensembleService.scheduleEnsemble("Notifying Ensemble", ZonedDateTime.of(2021, 11, 10, 17, 0, 0, 0, ZoneOffset.UTC));

        mockEnsembleScheduledNotifier.verify();
    }

    @Test
    void ensembleScheduledWithFailureToAutoCreateZoomLinkThenNotificationIsStillSent() throws Exception {
        MockEnsembleScheduledNotifier mockEnsembleScheduledNotifier = new MockEnsembleScheduledNotifier();
        EnsembleService ensembleService = new EnsembleService(new InMemoryEnsembleRepository(),
                                                              new InMemoryMemberRepository(),
                                                              mockEnsembleScheduledNotifier,
                                                              new FailsToCreateMeetingConferenceScheduler());

        ensembleService.scheduleEnsembleWithVideoConference("Ensemble failed to create Zoom link",
                                                            ZonedDateTime.of(2022, 3, 14, 15, 0, 0, 0, ZoneOffset.UTC));

        mockEnsembleScheduledNotifier.verify();
    }

    @Test
    void whenEnsembleScheduledWithZoomLinkEnsembleOpenNotificationIsSent() throws Exception {
        MockEnsembleScheduledNotifier mockEnsembleScheduledNotifier = new MockEnsembleScheduledNotifier();
        EnsembleService ensembleService = new EnsembleService(new InMemoryEnsembleRepository(),
                                                              new InMemoryMemberRepository(),
                                                              mockEnsembleScheduledNotifier, new DummyVideoConferenceScheduler());

        ensembleService.scheduleEnsemble("Notifying Ensemble",
                                         URI.create("https://zoom.us"),
                                         ZonedDateTime.of(2021, 11, 10, 17, 0, 0, 0, ZoneOffset.UTC));

        mockEnsembleScheduledNotifier.verify();
    }

    private static class MockEnsembleScheduledNotifier implements Notifier {
        private int statusValue = 0;

        @Override
        public int ensembleScheduled(Ensemble ensemble, URI registrationLink) {
            statusValue++;
            return statusValue;
        }

        @Override
        public void memberAccepted(Ensemble ensemble, Member member) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void ensembleCompleted(Ensemble ensemble) {
            throw new UnsupportedOperationException();
        }

        public void verify() {
            assertThat(statusValue)
                    .isEqualTo(1);
        }
    }
}

