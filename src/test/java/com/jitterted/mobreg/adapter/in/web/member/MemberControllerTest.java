package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.EnsembleServiceFactory;
import com.jitterted.mobreg.application.MemberFactory;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
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
        InMemoryEnsembleRepository huddleRepository = new InMemoryEnsembleRepository();
        huddleRepository.save(new Ensemble("GET Test", ZonedDateTime.now()));
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(huddleRepository);

        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        Member member = MemberFactory.createMember(11, "name", "ghuser");
        memberRepository.save(member);
        MemberService memberService = new MemberService(memberRepository);

        MemberController memberController = new MemberController(ensembleService, memberService);

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
        InMemoryEnsembleRepository huddleRepository = new InMemoryEnsembleRepository();
        Ensemble ensemble = huddleRepository.save(new Ensemble("Test", ZonedDateTime.now()));
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        EnsembleService ensembleService = new EnsembleService(huddleRepository, memberRepository, new DummyNotifier());
        MemberController memberController = new MemberController(ensembleService, CRASH_TEST_DUMMY_MEMBER_SERVICE);

        MemberRegisterForm memberRegisterForm = createMemberFormFor(ensemble, memberRepository);
        String redirectPage = memberController.accept(memberRegisterForm);

        assertThat(redirectPage)
                .isEqualTo("redirect:/member/register");

        assertThat(ensemble.acceptedMembers())
                .extracting(MemberId::id)
                .containsOnly(memberRegisterForm.getMemberId());
    }

    @Test
    public void memberDeclinesWillBeDeclinedForHuddle() throws Exception {
        InMemoryEnsembleRepository huddleRepository = new InMemoryEnsembleRepository();
        Ensemble ensemble = huddleRepository.save(new Ensemble("Test", ZonedDateTime.now()));
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        EnsembleService ensembleService = new EnsembleService(huddleRepository, memberRepository, new DummyNotifier());
        MemberController memberController = new MemberController(ensembleService, CRASH_TEST_DUMMY_MEMBER_SERVICE);

        MemberRegisterForm memberRegisterForm = createMemberFormFor(ensemble, memberRepository);
        String redirectPage = memberController.decline(memberRegisterForm);

        assertThat(redirectPage)
                .isEqualTo("redirect:/member/register");
        assertThat(ensemble.isDeclined(MemberId.of(memberRegisterForm.getMemberId())))
                .isTrue();
    }

    @NotNull
    private MemberRegisterForm createMemberFormFor(Ensemble ensemble, MemberRepository memberRepository) {
        MemberRegisterForm memberRegisterForm = new MemberRegisterForm();
        memberRegisterForm.setHuddleId(ensemble.getId().id());

        Member member = MemberFactory.createMember(8, "name", "username");
        memberRepository.save(member);
        memberRegisterForm.setMemberId(member.getId().id());

        return memberRegisterForm;
    }

}