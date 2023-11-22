package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MemberServiceLookupByUsernameTest {

    @Test
    void findingGithubUsernameWithUppercaseLettersFoundInAllLowercaseDatabase() throws Exception {
        MemberService memberService = new DefaultMemberService(new InMemoryMemberRepository());
        memberService.save(new Member("Mixed", "mixedcase", "ROLE_USER", "ROLE_MEMBER"));

        assertThat(memberService.findByGithubUsername("mIxEdCASE"))
                .isNotNull()
                .extracting(Member::githubUsername)
                .isEqualTo("mixedcase");
    }

}
