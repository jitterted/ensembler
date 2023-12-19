package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.in.web.OAuth2UserFactory;
import com.jitterted.mobreg.application.DefaultMemberService;
import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.EnsembleServiceFactory;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.TestEnsembleServiceBuilder;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleFactory;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberFactory;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.ZonedDateTimeFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings({"ConstantConditions", "unchecked"})
class AdminDashboardControllerTest {

    @Test
    void givenOneEnsembleResultsInEnsembleInViewModel() throws Exception {
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        Member member = MemberFactory.createMember(0, "ted", "tedyoung");
        memberRepository.save(member);
        MemberService memberService = new DefaultMemberService(memberRepository);
        InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository);
        ensembleRepository.save(new Ensemble("Name", ZonedDateTime.now()));
        AdminDashboardController adminDashboardController = new AdminDashboardController(ensembleService, memberService);

        Model model = new ConcurrentModel();
        OAuth2User oAuth2UserWithMemberRole = OAuth2UserFactory.createOAuth2UserWithMemberRole("tedyoung", "ROLE_MEMBER");
        SecurityContextImpl securityContext = new SecurityContextImpl(new OAuth2AuthenticationToken(oAuth2UserWithMemberRole,
                                                                                                    Set.of(new SimpleGrantedAuthority("ROLE_MEMBER")),
                                                                                                    "github"));
        adminDashboardController.dashboardView(model, oAuth2UserWithMemberRole, securityContext);

        List<EnsembleSummaryView> ensembleSummaryViews = (List<EnsembleSummaryView>) model.getAttribute("ensembles");
        assertThat(ensembleSummaryViews)
                .hasSize(1);
    }

    @Test
    void scheduleNewEnsembleResultsInEnsembleCreatedInRepository() throws Exception {
        InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        AdminDashboardController adminDashboardController = createAdminDashboardController(ensembleRepository);

        String pageName = adminDashboardController.scheduleEnsemble(new ScheduleEnsembleForm(
                "Name", "https://zoom.us/j/123456?pwd=12345", "2021-04-30", "09:00", "America/Los_Angeles"));

        assertThat(pageName)
                .isEqualTo("redirect:/admin/dashboard");
        assertThat(ensembleRepository.findAll())
                .hasSize(1);
    }

    @Test
    void changeExistingEnsembleResultsInChangesSaved() throws Exception {
        InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        Ensemble ensemble = new Ensemble("Old Name", ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 30, 9));
        ensembleRepository.save(ensemble);
        AdminDashboardController adminDashboardController = createAdminDashboardController(ensembleRepository);

        ScheduleEnsembleForm scheduleEnsembleForm = new ScheduleEnsembleForm("New Name", "https://zoom.us", "2021-12-01", "10:00", "America/Los_Angeles");
        EnsembleId ensembleId = ensemble.getId();
        String pageName = adminDashboardController.changeEnsemble(scheduleEnsembleForm, ensembleId.id());

        assertThat(pageName)
                .isEqualTo("redirect:/admin/ensemble/" + ensembleId.id());
        Ensemble expectedEnsemble = new Ensemble("New Name", ZonedDateTime.of(2021, 12, 1, 10, 0, 0, 0, ZoneId.of("America/Los_Angeles")).withZoneSameInstant(ZoneOffset.UTC));
        expectedEnsemble.setId(ensembleId);
        assertThat(ensembleRepository.findById(ensembleId).get())
                .usingRecursiveComparison()
                .isEqualTo(expectedEnsemble);
    }


    @Test
    void completeEnsembleCompletesTheEnsembleWithRecordingLinkAndRedirects() throws Exception {
        EnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        Ensemble ensemble = new Ensemble("to be completed", ZonedDateTime.now());
        ensemble.setId(EnsembleId.of(19));
        ensembleRepository.save(ensemble);
        AdminDashboardController adminDashboardController = createAdminDashboardController(ensembleRepository);

        String pageName = adminDashboardController.completeEnsemble(19, new CompleteEnsembleForm("https://recording.link/19"));

        assertThat(pageName)
                .isEqualTo("redirect:/admin/ensemble/19");

        assertThat(ensemble.isCompleted())
                .isTrue();
        assertThat(ensemble.recordingLink().toString())
                .isEqualTo("https://recording.link/19");
    }

    @Test
    void cancelEnsembleThenEnsembleIsCanceled() throws Exception {
        TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                .saveEnsemble(EnsembleFactory.withStartTimeNow());
        long ensembleId = builder.lastSavedEnsembleId().id();
        Ensemble ensemble = builder.lastSavedEnsemble();
        AdminDashboardController adminDashboardController = new AdminDashboardController(builder.build(), builder.memberService());

        String pageName = adminDashboardController.cancelEnsemble(ensembleId);

        assertThat(pageName)
                .isEqualTo("redirect:/admin/ensemble/" + ensembleId);

        assertThat(ensemble.isCanceled())
                .isTrue();
    }

    @Test
    @Deprecated
    void manuallyRegisterExistingMemberForEnsemble() throws Exception {
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        Member member1 = MemberFactory.createMember(0, "ted", "tedyoung");
        memberRepository.save(member1);
        Member member2 = MemberFactory.createMember(1, "two", "githubtwo");
        memberRepository.save(member2);
        MemberService memberService = new DefaultMemberService(memberRepository);
        InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository, memberRepository);
        Ensemble ensemble = new Ensemble("Manual Registered Ensemble", ZonedDateTime.now());
        ensemble.setId(EnsembleId.of(23));
        ensembleRepository.save(ensemble);
        AdminDashboardController adminDashboardController = new AdminDashboardController(ensembleService, memberService);

        AdminRegistrationForm form = new AdminRegistrationForm(ensemble.getId());
        form.setGithubUsername("githubtwo");
        adminDashboardController.registerParticipant(form);

        assertThat(ensemble.acceptedMembers())
                .containsExactly(MemberId.of(1));
    }

    @NotNull
    private AdminDashboardController createAdminDashboardController(EnsembleRepository ensembleRepository) {
        MemberService memberService = new DefaultMemberService(new InMemoryMemberRepository());
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository);
        return new AdminDashboardController(ensembleService, memberService);
    }

}
