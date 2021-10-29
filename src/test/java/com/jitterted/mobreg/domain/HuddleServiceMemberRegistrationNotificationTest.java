package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.InMemoryHuddleRepository;
import com.jitterted.mobreg.domain.port.Notifier;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleServiceMemberRegistrationNotificationTest {

    @Test
    public void memberRegistersForHuddleThenReceivesEmailWithHuddleDetailInfo() throws Exception {
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        Huddle huddle = new Huddle("scheduled",
                                   URI.create("https://zoom.us"),
                                   ZonedDateTime.of(2021, 10, 20, 16, 0, 0, 0, ZoneOffset.UTC));
        HuddleId huddleId = HuddleId.of(7L);
        huddle.setId(huddleId);
        huddleRepository.save(huddle);
        MemberBuilder memberBuilder = new MemberBuilder();
        MemberId memberId = memberBuilder.withFirstName("Fake")
                                         .withEmail("fake@example.com")
                                         .build()
                                         .getId();
        SpyEmailNotifier spyEmailNotifier = new SpyEmailNotifier();
        HuddleService huddleService = new HuddleService(huddleRepository, memberBuilder.memberRepository(), spyEmailNotifier);

        huddleService.registerMember(huddleId, memberId);

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
        public int newHuddleOpened(String description, URI registrationLink) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void memberRegistered(Huddle huddle, Member member) {
            emailBody = String.join(", ",
                                    huddle.name(),
                                    huddle.zoomMeetingLink().toString(),
                                    huddle.startDateTime().toString(),
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