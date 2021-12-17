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

class EnsembleServiceEnsembleScheduledNotificationTest {

    @Test
    public void whenEnsembleScheduledEnsembleOpenNotificationIsSent() throws Exception {
        MockNotifier mockNotifier = new MockNotifier();
        EnsembleService ensembleService = new EnsembleService(new InMemoryEnsembleRepository(),
                                                              new InMemoryMemberRepository(),
                                                              mockNotifier, new DummyVideoConferenceScheduler());

        ensembleService.scheduleEnsemble("Notifying Ensemble", ZonedDateTime.of(2021, 11, 10, 17, 0, 0, 0, ZoneOffset.UTC));

        mockNotifier.verify();
    }

    @Test
    public void whenEnsembleScheduledWithZoomLinkEnsembleOpenNotificationIsSent() throws Exception {
        MockNotifier mockNotifier = new MockNotifier();
        EnsembleService ensembleService = new EnsembleService(new InMemoryEnsembleRepository(),
                                                              new InMemoryMemberRepository(),
                                                              mockNotifier, new DummyVideoConferenceScheduler());

        ensembleService.scheduleEnsemble("Notifying Ensemble",
                                         URI.create("https://zoom.us"),
                                         ZonedDateTime.of(2021, 11, 10, 17, 0, 0, 0, ZoneOffset.UTC));

        mockNotifier.verify();
    }

    private static class MockNotifier implements Notifier {
        private int statusValue = -1;

        @Override
        public int ensembleScheduled(Ensemble ensemble, URI registrationLink) {
            statusValue = 1;
            return statusValue;
        }

        @Override
        public void memberAccepted(Ensemble ensemble, Member member) {
            throw new UnsupportedOperationException();
        }

        public void verify() {
            assertThat(statusValue)
                    .isEqualTo(1);
        }
    }
}

