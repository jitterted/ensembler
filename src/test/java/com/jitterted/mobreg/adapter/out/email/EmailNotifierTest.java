package com.jitterted.mobreg.adapter.out.email;

import com.jitterted.mobreg.application.MemberBuilder;
import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EmailNotifierTest {

    @Test
    public void memberWithEmailWhenNotificationOccursThenEmailSentToMember() throws Exception {
        MemberBuilder memberBuilder = new MemberBuilder();
        memberBuilder.withEmail("name@example.com")
                     .build();
        SpyEmailer spyEmailer = new SpyEmailer();
        Notifier notifier = new EmailNotifier(memberBuilder.memberService(), spyEmailer);

        notifier.newHuddleOpened("New Huddle", URI.create("https://mobreg.herokuapp.com/"));

        assertThat(spyEmailer.emailRecipients())
                .containsExactly("name@example.com");
        assertThat(spyEmailer.subject())
                .isEqualTo("Ensembler Notification: New Ensemble Scheduled");
        assertThat(spyEmailer.body())
                .isEqualTo("""
                                   New Ensemble 'New Huddle' has been scheduled.
                                   <br/>
                                   Visit <a href="https://mobreg.herokuapp.com/">MobReg</a> to register.
                                   """);
    }

    @Test
    public void memberWithEmailRegistersThenEmailSentToMemberWithHuddleDetails() throws Exception {
        Member member = new MemberBuilder().withFirstName("FirstName")
                                           .withEmail("name@example.com")
                                           .build();
        Huddle huddle = new Huddle("Ensemble #123", URI.create("https://zoom.us"), ZonedDateTime.of(2021, 10, 20, 16, 0, 0, 0, ZoneOffset.UTC));
        SpyEmailer spyEmailer = new SpyEmailer();
        Notifier notifier = new EmailNotifier(null, spyEmailer);

        notifier.memberRegistered(huddle, member);

        assertThat(spyEmailer.emailRecipients())
                .containsExactly("name@example.com");
        assertThat(spyEmailer.body())
                .isEqualTo("""
                           Hi FirstName,
                           
                           You have registered for the 'Ensemble #123', which happens on 2021-10-20T16:00Z.
                           Click <a href="https://zoom.us">here</a> to join the Zoom. You can add this event to your Google Calendar
                           by clicking <a href="https://calendar.google.com/calendar/render?action=TEMPLATE&text=Ensemble+%23123&dates=20211020T160000Z/20211020T180000Z&details=Zoom+link+is%3A+%3Ca+href%3D%27https%3A%2F%2Fzoom.us%27%3Ehttps%3A%2F%2Fzoom.us%3C%2Fa%3E">here</a>.
                           """);
    }

    @Test
    public void memberWithoutEmailRegistersThenNoEmailIsSent() throws Exception {
        Member member = new MemberBuilder().withNoEmail().build();
        Huddle huddle = new Huddle("Doesn't matter", URI.create("https://whocar.es"), ZonedDateTime.now());
        SpyEmailer spyEmailer = new SpyEmailer();
        Notifier notifier = new EmailNotifier(null, spyEmailer);

        notifier.memberRegistered(huddle, member);

        assertThat(spyEmailer.body())
                .isNull();
    }

}