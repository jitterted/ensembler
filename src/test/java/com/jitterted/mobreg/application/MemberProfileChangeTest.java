package com.jitterted.mobreg.application;

import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static org.assertj.core.api.Assertions.*;

class MemberProfileChangeTest {

    @Test
    public void changesToMemberProfileAreSaved() throws Exception {
        TestMemberBuilder testMemberBuilder = new TestMemberBuilder();
        Member member = testMemberBuilder.withFirstName("")
                                         .withNoEmail()
                                         .withTimezone("Z")
                                         .withGithubUsername("immutable_username")
                                         .buildAndSave();
        MemberService memberService = testMemberBuilder.memberService();

        memberService.changeFirstName(member, "Firsty");
        Member foundMember1 = memberService.findById(member.getId());
        assertThat(foundMember1.firstName())
                .isEqualTo("Firsty");

        memberService.changeEmail(member, "new@example.com");
        Member foundMember2 = memberService.findById(member.getId());
        assertThat(foundMember2.email())
                .isEqualTo("new@example.com");

        memberService.changeTimeZone(member, "America/New_York");
        Member foundMember3 = memberService.findById(member.getId());
        assertThat(foundMember3.timeZone())
                .isEqualTo(ZoneId.of("America/New_York"));
    }

}