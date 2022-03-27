package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MemberLookupTest {

    @Test
    public void findAllMembersReturnsAllRegisteredMembers() throws Exception {
        MemberService memberService = new MemberService(new InMemoryMemberRepository());
        MemberLookup memberLookup = new MemberLookup(memberService);
        memberService.save(new Member("Admin", "admin_github", "ROLE_ADMIN", "ROLE_USER", "ROLE_MEMBER"));
        memberService.save(new Member("Member1", "member1_github", "ROLE_USER", "ROLE_MEMBER"));
        memberService.save(new Member("SimpleUser", "simple_user_github", "ROLE_USER"));

        List<Member> allMembers = memberLookup.findAll();
        allMembers.forEach(System.out::println);

        assertThat(allMembers)
            .isNotEmpty()
            .extracting(Member::firstName)
            .containsExactly("Admin", "Member1", "SimpleUser");
    }
}
