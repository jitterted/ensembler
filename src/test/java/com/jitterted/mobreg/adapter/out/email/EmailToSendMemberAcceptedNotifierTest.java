package com.jitterted.mobreg.adapter.out.email;

import com.jitterted.mobreg.application.MemberBuilder;
import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EmailToSendMemberAcceptedNotifierTest {

    @Test
    public void memberWithEmailAcceptsThenEmailSentToMemberWithEnsembleDetails() throws Exception {
        Member member = new MemberBuilder().withFirstName("FirstName")
                                           .withEmail("name@example.com")
                                           .withTimezone("America/Los_Angeles")
                                           .build();
        Ensemble ensemble = new Ensemble("Ensemble #123", URI.create("https://zoom.us"), ZonedDateTime.of(2021, 10, 20, 16, 0, 0, 0, ZoneOffset.UTC));
        SpyEmailer spyEmailer = new SpyEmailer();
        Notifier notifier = new EmailNotifier(null, spyEmailer);

        notifier.memberAccepted(ensemble, member);

        assertThat(spyEmailer.sentEmails())
                .containsExactly(new EmailToSend("Ensembler Notification: Registration Confirmation",
                                                 """
                                                   Hi FirstName,
                                                                              
                                                   You have registered for 'Ensemble #123', which happens on October 20, 2021 at 9:00AM (PDT).
                                                   Click <a href="https://zoom.us">here</a> to join the Zoom. You can add this event to your Google Calendar
                                                   by clicking <a href="https://calendar.google.com/calendar/render?action=TEMPLATE&text=Ensemble+%23123&dates=20211020T160000Z/20211020T180000Z&details=Zoom+link+is%3A+%3Ca+href%3D%27https%3A%2F%2Fzoom.us%27%3Ehttps%3A%2F%2Fzoom.us%3C%2Fa%3E">here</a>.
                                                   """
                        , "name@example.com"));
    }

    @Test
    public void memberWithoutEmailRegistersThenNoEmailIsSent() throws Exception {
        Member member = new MemberBuilder().withNoEmail().build();
        Ensemble ensemble = new Ensemble("Doesn't matter", URI.create("https://whocar.es"), ZonedDateTime.now());
        SpyEmailer spyEmailer = new SpyEmailer();
        Notifier notifier = new EmailNotifier(null, spyEmailer);

        notifier.memberAccepted(ensemble, member);

        assertThat(spyEmailer.sentEmails())
                .isEmpty();
    }

}