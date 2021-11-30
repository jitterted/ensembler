package com.jitterted.mobreg.application;

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
    public void memberRegistersForHuddleThenReceivesEmailWithHuddleDetailInfo() throws Exception {
        InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        Ensemble ensemble = new Ensemble("scheduled",
                                         URI.create("https://zoom.us"),
                                         ZonedDateTime.of(2021, 10, 20, 16, 0, 0, 0, ZoneOffset.UTC));
        EnsembleId ensembleId = EnsembleId.of(7L);
        ensemble.setId(ensembleId);
        ensembleRepository.save(ensemble);
        MemberBuilder memberBuilder = new MemberBuilder();
        MemberId memberId = memberBuilder.withFirstName("Fake")
                                         .withEmail("fake@example.com")
                                         .build()
                                         .getId();
        SpyEmailNotifier spyEmailNotifier = new SpyEmailNotifier();
        EnsembleService ensembleService = new EnsembleService(ensembleRepository, memberBuilder.memberRepository(), spyEmailNotifier);

        ensembleService.registerMember(ensembleId, memberId);

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
        public int ensembleScheduled(String description, URI registrationLink) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void memberRegistered(Ensemble ensemble, Member member) {
            emailBody = String.join(", ",
                                    ensemble.name(),
                                    ensemble.zoomMeetingLink().toString(),
                                    ensemble.startDateTime().toString(),
                                    "https://calendar.google.com",
                                    member.firstName());
            emailAddress = member.email();
        }

        public String emailBody() {
            return emailBody;
        }

        public String emailAddress() {
            return emailAddress;
        }
    }
}