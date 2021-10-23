package com.jitterted.mobreg.adapter.out.email;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberService;
import com.jitterted.mobreg.domain.port.MemberRepository;
import com.jitterted.mobreg.domain.port.Notifier;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EmailNotifierTest {

    @Test
    public void memberWithEmailWhenNotificationOccursThenEmailSentToMember() throws Exception {
        MemberRepository memberRepository = new InMemoryMemberRepository();
        Member member = new Member("hasEmail", "githubusername", "ROLE_MEMBER");
        member.changeEmailTo("name@example.com");
        memberRepository.save(member);
        memberRepository.save(new Member("noEmail", "noemailuser", "ROLE_MEMBER"));
        MemberService memberService = new MemberService(memberRepository);
        SpyEmailer spyEmailer = new SpyEmailer();
        Notifier notifier = new EmailNotifier(memberService, spyEmailer);

        notifier.newHuddleOpened("New Huddle", URI.create("https://example.com"));

        assertThat(spyEmailer.emailRecipients())
                .containsExactly("name@example.com");
        assertThat(spyEmailer.subject())
                .isEqualTo("Ensembler Notification: New Ensemble Scheduled");
        assertThat(spyEmailer.body())
                .isEqualTo("""
                                   New Ensemble 'New Huddle' has been scheduled.
                                   Visit https://example.com to register.
                                   """);
    }

    @Test
    public void memberWithEmailRegistersThenEmailSentToMemberWithHuddleDetails() throws Exception {
        Member member = new Member("FirstName", "githubusername", "ROLE_MEMBER");
        member.changeEmailTo("name@example.com");
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
                           The Zoom link is https://zoom.us and you can add this event to your Google Calendar
                           by clicking on this link: https://calendar.google.com/calendar/render.
                           """);
    }

    @Test
    public void memberWithoutEmailRegistersThenNoEmailIsSent() throws Exception {
        Member member = new Member("NoEmail", "githubusername", "ROLE_MEMBER");
        Huddle huddle = new Huddle("Doesn't matter", URI.create("https://whocar.es"), ZonedDateTime.now());
        SpyEmailer spyEmailer = new SpyEmailer();
        Notifier notifier = new EmailNotifier(null, spyEmailer);

        notifier.memberRegistered(huddle, member);

        assertThat(spyEmailer.body())
                .isNull();
    }

}