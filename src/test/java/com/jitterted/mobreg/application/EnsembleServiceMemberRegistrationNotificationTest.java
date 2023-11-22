package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.DummyVideoConferenceScheduler;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleServiceMemberRegistrationNotificationTest {

    @Test
    void memberRegistersForEnsembleThenReceivesEmailWithEnsembleDetailInfo() throws Exception {
        InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        Ensemble ensemble = new Ensemble("scheduled",
                                         URI.create("https://zoom.us"),
                                         ZonedDateTime.of(2021, 10, 20, 16, 0, 0, 0, ZoneOffset.UTC));
        EnsembleId ensembleId = EnsembleId.of(7L);
        ensemble.setId(ensembleId);
        ensembleRepository.save(ensemble);
        TestMemberBuilder memberBuilder = new TestMemberBuilder();
        MemberId memberId = memberBuilder.withFirstName("Fake")
                                         .withEmail("fake@example.com")
                                         .buildAndSave()
                                         .getId();
        SpyEmailNotifier spyEmailNotifier = new SpyEmailNotifier();
        EnsembleService ensembleService = new EnsembleService(ensembleRepository, memberBuilder.memberRepository(),
                                                              spyEmailNotifier, new DummyVideoConferenceScheduler());

        ensembleService.acceptMember(ensembleId, memberId);

        assertThat(spyEmailNotifier.emailBody())
                .contains("https://zoom.us",
                          "scheduled",
                          "2021-10-20T16:00Z",
                          "https://calendar.google.com",
                          "Fake");

        assertThat(spyEmailNotifier.emailAddress())
                .isEqualTo("fake@example.com");
    }

    private static class SpyEmailNotifier implements Notifier {
        private String emailBody;
        private String emailAddress;

        @Override
        public int ensembleScheduled(Ensemble ensemble, URI registrationLink) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void memberAccepted(Ensemble ensemble, Member member) {
            emailBody = String.join(", ",
                                    ensemble.name(),
                                    ensemble.meetingLink().toString(),
                                    ensemble.startDateTime().toString(),
                                    "https://calendar.google.com",
                                    member.firstName());
            emailAddress = member.email();
        }

        @Override
        public void ensembleCompleted(Ensemble ensemble) {
            throw new UnsupportedOperationException();
        }

        public String emailBody() {
            return emailBody;
        }

        public String emailAddress() {
            return emailAddress;
        }
    }
}