package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.domain.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberFactory;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberService;
import com.jitterted.mobreg.domain.OAuth2UserFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.*;

public class MemberProfileControllerTest {

    private static final @NotNull DefaultOAuth2User USER_WITH_MEMBER_ROLE = OAuth2UserFactory.createOAuth2UserWithMemberRole("ghmember", "ROLE_MEMBER");

    @Test
    public void profilePrepareFormFullyPopulatesForm() throws Exception {
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        Member member = MemberFactory.createMember(13, "first", "ghmember");
        member.changeEmailTo("member@example.com");
        memberRepository.save(member);
        MemberService memberService = new MemberService(memberRepository);

        MemberProfileController memberProfileController = new MemberProfileController(memberService);

        Model model = new ConcurrentModel();
        memberProfileController.prepareMemberProfileForm(model, USER_WITH_MEMBER_ROLE);

        assertThat((String) model.getAttribute("githubUsername"))
                .isEqualTo("ghmember");
        assertThat((String) model.getAttribute("firstName"))
                .isEqualTo("first");

        MemberProfileForm memberProfileForm = (MemberProfileForm) model.getAttribute("profile");

        assertThat(memberProfileForm.getEmail())
                .isEqualTo("member@example.com");
    }

    @Test
    public void changedEmailInFormThenUpdatesMemberEmailAndRedirectsToProfileView() throws Exception {
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        Member member = MemberFactory.createMember(13, "first", "ghmember");
        MemberId memberId = memberRepository.save(member).getId();
        MemberService memberService = new MemberService(memberRepository);

        MemberProfileController memberProfileController = new MemberProfileController(memberService);

        MemberProfileForm memberProfileForm = new MemberProfileForm(member.firstName(), member.githubUsername(), "new@example.com");
        String redirectPage = memberProfileController.updateProfileFromForm(memberProfileForm, USER_WITH_MEMBER_ROLE);

        assertThat(redirectPage)
                .isEqualTo("redirect:/member/profile");

        Member memberFound = memberService.findById(memberId);
        assertThat(memberFound.email())
                .isEqualTo("new@example.com");
    }

}
