package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.in.web.OAuth2UserFactory;
import com.jitterted.mobreg.application.DefaultMemberService;
import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.EnsembleServiceFactory;
import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.port.DummyNotifier;
import com.jitterted.mobreg.application.port.DummyVideoConferenceScheduler;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberFactory;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberStatus;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("unchecked")
class MemberControllerTest {

    @Nested
    class ViewModels {

        @Test
        void registerViewShowsInProgressAndPastAndFutureEnsembles() {
            Ensemble upcomingEnsemble = new Ensemble("Future Ensemble",
                                                     ZonedDateTime.now().plusDays(2));
            Fixture fixture = createFixture(upcomingEnsemble);
            AuthFixture authFixture = createAuthUser(fixture.memberService);
            Ensemble ensemble1 = fixture.ensembleService()
                                        .scheduleEnsemble("Past Ensemble 1",
                                                          ZonedDateTime.now().minusDays(12));
            fixture.ensembleService().joinAsParticipant(ensemble1.getId(), authFixture.memberId);
            Ensemble ensemble2 = fixture.ensembleService()
                                        .scheduleEnsemble("Past Ensemble 2",
                                                          ZonedDateTime.now().minusDays(5));
            fixture.ensembleService().joinAsSpectator(ensemble2.getId(), authFixture.memberId);
            Ensemble inProgressEnsemble = fixture.ensembleService()
                                                 .scheduleEnsemble("In Progress",
                                                                   ZonedDateTime.now().minusMinutes(5));
            fixture.ensembleService()
                   .joinAsParticipant(inProgressEnsemble.getId(), authFixture.memberId);

            Model model = new ConcurrentModel();
            fixture.memberController.showEnsemblesForUser(model,
                                                          authFixture.oAuth2UserWithMemberRole(),
                                                          authFixture.securityContext());

            List<EnsembleSummaryView> upcomingEnsembles = (List<EnsembleSummaryView>) model.getAttribute("upcomingEnsembles");
            assertThat(upcomingEnsembles)
                    .as("Should be 1 Upcoming Ensemble in the Model")
                    .hasSize(1);
            List<EnsembleSummaryView> pastEnsembles = (List<EnsembleSummaryView>) model.getAttribute("pastEnsembles");
            assertThat(pastEnsembles)
                    .as("Should be 2 Past Ensembles in the Model")
                    .hasSize(2);
            Optional<InProgressEnsembleView> inProgressEnsembleViewOptional = (Optional<InProgressEnsembleView>) model.getAttribute("inProgressEnsembleViewOptional");
            assertThat(inProgressEnsembleViewOptional)
                    .isPresent()
                    .get()
                    .isExactlyInstanceOf(InProgressEnsembleView.class);
        }

        @Test
        void ensembleFormContainsMemberIdForOAuth2User() throws Exception {
            InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
            ensembleRepository.save(new Ensemble("GET Test", ZonedDateTime.now()));
            EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository);

            InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
            MemberService memberService = new DefaultMemberService(memberRepository);

            EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(ensembleRepository, memberRepository);
            MemberController memberController = new MemberController(ensembleService, memberService, ensembleTimerHolder);

            Model model = new ConcurrentModel();
            AuthFixture authFixture = createAuthUser(memberService);
            memberController.showEnsemblesForUser(model,
                                                  authFixture.oAuth2UserWithMemberRole(),
                                                  authFixture.securityContext());

            assertThat((String) model.getAttribute("firstName"))
                    .isEqualTo("name");
            assertThat((String) model.getAttribute("githubUsername"))
                    .isEqualTo("ghuser");

            MemberRegisterForm memberRegisterForm = (MemberRegisterForm) model.getAttribute("register");

            assertThat(memberRegisterForm.getMemberId())
                    .isEqualTo(11);
        }
    }

    @Nested
    class Commands {

        @Test
        void memberRegistersForEnsembleWillBeRegisteredForThatEnsemble() throws Exception {
            Fixture fixture = createFixture(new Ensemble("Test", ZonedDateTime.now()));

            MemberRegisterForm memberRegisterForm = createMemberFormFor(fixture);
            String redirectPage = fixture.memberController().accept(memberRegisterForm);

            assertThat(redirectPage)
                    .isEqualTo("redirect:/member/register");

            assertThat(fixture.ensemble().participants())
                    .extracting(MemberId::id)
                    .containsOnly(memberRegisterForm.getMemberId());
        }

        @Test
        void memberDeclinesWillBeDeclinedForEnsemble() throws Exception {
            Fixture fixture = createFixture(new Ensemble("Test", ZonedDateTime.now()));

            MemberRegisterForm memberRegisterForm = createMemberFormFor(fixture);
            String redirectPage = fixture.memberController().decline(memberRegisterForm);

            assertThat(redirectPage)
                    .isEqualTo("redirect:/member/register");
            assertThat(fixture.ensemble()
                              .memberStatusFor(MemberId.of(memberRegisterForm.getMemberId())))
                    .isEqualByComparingTo(MemberStatus.DECLINED);
        }

        @Test
        void memberJoiningAsSpectatorBecomesSpectator() throws Exception {
            Fixture fixture = createFixture(new Ensemble("Test", ZonedDateTime.now()));

            MemberRegisterForm memberRegisterForm = createMemberFormFor(fixture);
            String redirectPage = fixture.memberController().joinAsSpectator(memberRegisterForm);

            assertThat(redirectPage)
                    .isEqualTo("redirect:/member/register");
            assertThat(fixture.ensemble().spectators())
                    .containsExactly(MemberId.of(memberRegisterForm.getMemberId()));
        }
    }

    // -- encapsulated setup methods...

    @NotNull
    private static Fixture createFixture(Ensemble ensembleToSave) {
        InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        Ensemble ensemble = ensembleRepository.save(ensembleToSave);
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        EnsembleService ensembleService = new EnsembleService(ensembleRepository,
                                                              memberRepository,
                                                              new DummyNotifier(),
                                                              new DummyVideoConferenceScheduler());
        DefaultMemberService memberService = new DefaultMemberService(memberRepository);
        EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(ensembleRepository, memberRepository);
        MemberController memberController = new MemberController(ensembleService, memberService, ensembleTimerHolder);
        return new Fixture(ensemble, memberService, ensembleService, memberController);
    }

    private record Fixture(Ensemble ensemble,
                           MemberService memberService,
                           EnsembleService ensembleService,
                           MemberController memberController) {
    }

    @NotNull
    private MemberRegisterForm createMemberFormFor(Fixture fixture) {
        MemberRegisterForm memberRegisterForm = new MemberRegisterForm();
        memberRegisterForm.setEnsembleId(fixture.ensemble().getId().id());

        Member member = MemberFactory.createMember(8, "name", "username");
        fixture.memberService().save(member);
        memberRegisterForm.setMemberId(member.getId().id());

        return memberRegisterForm;
    }


    private static AuthFixture createAuthUser(MemberService memberService) {
        Member member = MemberFactory.createMember(11, "name", "ghuser");
        memberService.save(member);
        OAuth2User oAuth2UserWithMemberRole = OAuth2UserFactory.createOAuth2UserWithMemberRole("ghuser", "ROLE_MEMBER");
        SecurityContextImpl securityContext = new SecurityContextImpl(
                new OAuth2AuthenticationToken(oAuth2UserWithMemberRole,
                                              Set.of(new SimpleGrantedAuthority("ROLE_MEMBER")),
                                              "github"));
        return new AuthFixture(member.getId(), oAuth2UserWithMemberRole, securityContext);
    }

    private record AuthFixture(MemberId memberId,
                               OAuth2User oAuth2UserWithMemberRole,
                               SecurityContextImpl securityContext) {
    }

}
