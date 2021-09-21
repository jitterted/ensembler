package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.domain.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberFactory;
import com.jitterted.mobreg.domain.MemberService;
import com.jitterted.mobreg.domain.OAuth2UserFactory;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.*;

public class MemberProfileControllerTest {

    @Test
    public void profilePrepareFormFullyPopulatesForm() throws Exception {
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        Member member = MemberFactory.createMember(13, "first", "ghmember");
        member.changeEmailTo("member@example.com");
        memberRepository.save(member);
        MemberService memberService = new MemberService(memberRepository);

        MemberProfileController memberProfileController = new MemberProfileController(memberService);

        Model model = new ConcurrentModel();
        memberProfileController.prepareMemberProfileForm(model, OAuth2UserFactory.createOAuth2UserWithMemberRole("ghmember", "ROLE_MEMBER"));

        assertThat((String) model.getAttribute("githubUsername"))
                .isEqualTo("ghmember");
        assertThat((String) model.getAttribute("firstName"))
                .isEqualTo("first");

        MemberProfileForm memberProfileForm = (MemberProfileForm) model.getAttribute("profile");

        assertThat(memberProfileForm.getEmail())
                .isEqualTo("member@example.com");
    }

}
