package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.InMemoryHuddleRepository;
import com.jitterted.mobreg.domain.port.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.port.Notifier;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleServiceEnsembleScheduledNotificationTest {

    @Test
    public void whenHuddleScheduledEnsembleOpenNotificationIsSent() throws Exception {
        MockNotifier mockNotifier = new MockNotifier();
        HuddleService huddleService = new HuddleService(new InMemoryHuddleRepository(),
                                                        new InMemoryMemberRepository(),
                                                        mockNotifier);

        huddleService.scheduleHuddle("Notifying Ensemble", ZonedDateTime.of(2021, 11, 10, 17, 0, 0, 0, ZoneOffset.UTC));

        mockNotifier.verify();
    }

    @Test
    public void whenHuddleScheduledWithZoomLinkEnsembleOpenNotificationIsSent() throws Exception {
        MockNotifier mockNotifier = new MockNotifier();
        HuddleService huddleService = new HuddleService(new InMemoryHuddleRepository(),
                                                        new InMemoryMemberRepository(),
                                                        mockNotifier);

        huddleService.scheduleHuddle("Notifying Ensemble",
                                     URI.create("https://zoom.us"),
                                     ZonedDateTime.of(2021, 11, 10, 17, 0, 0, 0, ZoneOffset.UTC));

        mockNotifier.verify();
    }

    private static class MockNotifier implements Notifier {
        private int statusValue = -1;

        @Override
        public int newHuddleOpened(String description, URI registrationLink) {
            statusValue = 1;
            return statusValue;
        }

        @Override
        public void memberRegistered(Huddle huddle, Member member) {
            throw new UnsupportedOperationException();
        }

        public void verify() {
            assertThat(statusValue)
                    .isEqualTo(1);
        }
    }
}

