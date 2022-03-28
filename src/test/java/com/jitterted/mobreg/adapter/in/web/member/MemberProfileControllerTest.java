package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.in.web.OAuth2UserFactory;
import com.jitterted.mobreg.application.DefaultMemberService;
import com.jitterted.mobreg.domain.MemberFactory;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.TestMemberBuilder;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Member;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.ZoneId;

import static org.assertj.core.api.Assertions.*;

public class MemberProfileControllerTest {

    private static final String GITHUB_USERNAME = "ghmember";
    private static final @NotNull DefaultOAuth2User USER_WITH_MEMBER_ROLE = OAuth2UserFactory.createOAuth2UserWithMemberRole(GITHUB_USERNAME, "ROLE_MEMBER");

    @Test
    public void profilePrepareFormFullyPopulatesForm() throws Exception {
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        Member member = MemberFactory.createMember(13, "first", GITHUB_USERNAME);
        member.changeEmailTo("member@example.com");
        memberRepository.save(member);
        MemberService memberService = new DefaultMemberService(memberRepository);

        MemberProfileController memberProfileController = new MemberProfileController(memberService);

        Model model = new ConcurrentModel();
        memberProfileController.prepareMemberProfileForm(model, USER_WITH_MEMBER_ROLE);

        assertThat((String) model.getAttribute("githubUsername"))
                .isEqualTo(GITHUB_USERNAME);
        assertThat((String) model.getAttribute("firstName"))
                .isEqualTo("first");

        MemberProfileForm memberProfileForm = (MemberProfileForm) model.getAttribute("profile");

        assertThat(memberProfileForm.getEmail())
                .isEqualTo("member@example.com");
    }

    @Test
    public void changeProfileInfoOnFormUpdatesMemberProfileAndRedirectsToProfileView() throws Exception {
        TestMemberBuilder builder = new TestMemberBuilder()
                .withEmail("none@nowhere")
                .withGithubUsername(GITHUB_USERNAME)
                .withTimezone("Asia/Tokyo");
        Member member = builder.buildAndSave();
        MemberService memberService = builder.memberService();
        MemberProfileController memberProfileController = new MemberProfileController(memberService);

        MemberProfileForm memberProfileForm = new MemberProfileForm(member.firstName(), member.githubUsername(), "new@example.com", "America/Los_Angeles");
        String redirectPage = memberProfileController.updateProfileFromForm(memberProfileForm, USER_WITH_MEMBER_ROLE, new RedirectAttributesModelMap());

        assertThat(redirectPage)
                .isEqualTo("redirect:/member/profile");

        Member memberFound = memberService.findById(member.getId());
        assertThat(memberFound.email())
                .isEqualTo("new@example.com");
        assertThat(memberFound.timeZone())
                .isEqualTo(ZoneId.of("America/Los_Angeles"));
    }

}
