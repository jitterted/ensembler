package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleService;
import com.jitterted.mobreg.domain.InMemoryHuddleRepository;
import com.jitterted.mobreg.domain.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberFactory;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberService;
import com.jitterted.mobreg.domain.OAuth2UserFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class MemberControllerTest {

    private static final MemberService CRASH_TEST_DUMMY_MEMBER_SERVICE = null;

    @Test
    public void huddleFormContainsMemberIdForOAuth2User() throws Exception {
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        huddleRepository.save(new Huddle("GET Test", ZonedDateTime.now()));
        HuddleService huddleService = new HuddleService(huddleRepository);

        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        Member member = MemberFactory.createMember(11, "name", "ghuser");
        memberRepository.save(member);
        MemberService memberService = new MemberService(memberRepository);

        MemberController memberController = new MemberController(huddleService, memberService);

        Model model = new ConcurrentModel();
        memberController.showHuddlesForUser(model, OAuth2UserFactory.createOAuth2UserWithMemberRole("ghuser", "ROLE_MEMBER"));

        assertThat((String) model.getAttribute("firstName"))
                .isEqualTo("name");
        assertThat((String) model.getAttribute("githubUsername"))
                .isEqualTo("ghuser");

        MemberRegisterForm memberRegisterForm = (MemberRegisterForm) model.getAttribute("register");

        assertThat(memberRegisterForm.getMemberId())
                .isEqualTo(11);
    }

    @Test
    public void memberRegistersForHuddleWillBeRegisteredForThatHuddle() throws Exception {
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        Huddle huddle = huddleRepository.save(new Huddle("Test", ZonedDateTime.now()));
        HuddleService huddleService = new HuddleService(huddleRepository);
        MemberController memberController = new MemberController(huddleService, CRASH_TEST_DUMMY_MEMBER_SERVICE);

        MemberRegisterForm memberRegisterForm = createMemberFormFor(huddle);
        String redirectPage = memberController.register(memberRegisterForm);

        assertThat(redirectPage)
                .isEqualTo("redirect:/member/register");

        assertThat(huddle.registeredMembers())
                .extracting(MemberId::id)
                .containsOnly(memberRegisterForm.getMemberId());
    }

    @NotNull
    private MemberRegisterForm createMemberFormFor(Huddle huddle) {
        MemberRegisterForm memberRegisterForm = new MemberRegisterForm();
        memberRegisterForm.setHuddleId(huddle.getId().id());

        MemberId memberId = MemberFactory.createMemberReturningId(8, "name", "username");
        memberRegisterForm.setMemberId(memberId.id());

        return memberRegisterForm;
    }

}