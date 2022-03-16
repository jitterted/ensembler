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

        memberService.changeEmail(member, "new@example.com");
        memberService.changeTimeZone(member, "America/New_York");
        memberService.changeFirstName(member, "Firsty");

        Member foundMember = memberService.findById(member.getId());
        assertThat(foundMember.firstName())
                .isEqualTo("Firsty");
        assertThat(foundMember.email())
                .isEqualTo("new@example.com");
        assertThat(foundMember.timeZone())
                .isEqualTo(ZoneId.of("America/New_York"));
    }

}