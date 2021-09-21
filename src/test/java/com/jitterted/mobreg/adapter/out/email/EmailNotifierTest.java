package com.jitterted.mobreg.adapter.out.email;

import com.jitterted.mobreg.domain.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberService;
import com.jitterted.mobreg.domain.port.MemberRepository;
import com.jitterted.mobreg.domain.port.Notifier;
import org.junit.jupiter.api.Test;

import java.net.URI;

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

}