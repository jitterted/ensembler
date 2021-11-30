package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.EnsembleServiceFactory;
import com.jitterted.mobreg.application.MemberFactory;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.OAuth2UserFactory;
import com.jitterted.mobreg.domain.ZonedDateTimeFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings({"ConstantConditions", "unchecked"})
class AdminDashboardControllerTest {

    @Test
    public void givenOneHuddleResultsInHuddleInViewModel() throws Exception {
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        Member member = MemberFactory.createMember(0, "ted", "tedyoung");
        memberRepository.save(member);
        MemberService memberService = new MemberService(memberRepository);
        InMemoryEnsembleRepository huddleRepository = new InMemoryEnsembleRepository();
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(huddleRepository);
        huddleRepository.save(new Ensemble("Name", ZonedDateTime.now()));
        AdminDashboardController adminDashboardController = new AdminDashboardController(ensembleService, memberService);

        Model model = new ConcurrentModel();
        adminDashboardController.dashboardView(model, OAuth2UserFactory.createOAuth2UserWithMemberRole("tedyoung", "ROLE_MEMBER"));

        List<HuddleSummaryView> huddleSummaryViews = (List<HuddleSummaryView>) model.getAttribute("ensembles");
        assertThat(huddleSummaryViews)
                .hasSize(1);
    }

    @Test
    public void scheduleNewHuddleResultsInHuddleCreatedInRepository() throws Exception {
        InMemoryEnsembleRepository huddleRepository = new InMemoryEnsembleRepository();
        AdminDashboardController adminDashboardController = createAdminDashboardController(huddleRepository);

        String pageName = adminDashboardController.scheduleHuddle(new ScheduleHuddleForm(
                "Name", "https://zoom.us/j/123456?pwd=12345", "2021-04-30", "09:00", "America/Los_Angeles"));

        assertThat(pageName)
                .isEqualTo("redirect:/admin/dashboard");
        assertThat(huddleRepository.findAll())
                .hasSize(1);
    }

    @Test
    public void changeExistingHuddleResultsInChangesSaved() throws Exception {
        InMemoryEnsembleRepository huddleRepository = new InMemoryEnsembleRepository();
        Ensemble ensemble = new Ensemble("Old Name", ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 30, 9));
        huddleRepository.save(ensemble);
        AdminDashboardController adminDashboardController = createAdminDashboardController(huddleRepository);

        ScheduleHuddleForm scheduleHuddleForm = new ScheduleHuddleForm("New Name", null, "2021-12-01", "10:00", "America/Los_Angeles");
        EnsembleId ensembleId = ensemble.getId();
        String pageName = adminDashboardController.changeHuddle(scheduleHuddleForm, ensembleId.id());

        assertThat(pageName)
                .isEqualTo("redirect:/admin/huddle/" + ensembleId.id());
        Ensemble expectedEnsemble = new Ensemble("New Name", ZonedDateTime.of(2021, 12, 1, 10, 0, 0, 0, ZoneId.of("America/Los_Angeles")).withZoneSameInstant(ZoneOffset.UTC));
        expectedEnsemble.setId(ensembleId);
        assertThat(huddleRepository.findById(ensembleId).get())
                .usingRecursiveComparison()
                .isEqualTo(expectedEnsemble);
    }


    @Test
    public void completeHuddleCompletesTheHuddleWithRecordingLinkAndRedirects() throws Exception {
        InMemoryEnsembleRepository huddleRepository = new InMemoryEnsembleRepository();
        Ensemble ensemble = new Ensemble("to be completed", ZonedDateTime.now());
        ensemble.setId(EnsembleId.of(19));
        huddleRepository.save(ensemble);
        AdminDashboardController adminDashboardController = createAdminDashboardController(huddleRepository);

        String pageName = adminDashboardController.completeHuddle(19, new CompleteHuddleForm("https://recording.link/19"));

        assertThat(pageName)
                .isEqualTo("redirect:/admin/huddle/19");

        assertThat(ensemble.isCompleted())
                .isTrue();
        assertThat(ensemble.recordingLink().toString())
                .isEqualTo("https://recording.link/19");
    }

    @Test
    public void manuallyRegisterExistingMemberForHuddle() throws Exception {
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        Member member1 = MemberFactory.createMember(0, "ted", "tedyoung");
        memberRepository.save(member1);
        Member member2 = MemberFactory.createMember(1, "two", "githubtwo");
        memberRepository.save(member2);
        MemberService memberService = new MemberService(memberRepository);
        InMemoryEnsembleRepository huddleRepository = new InMemoryEnsembleRepository();
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(huddleRepository, memberRepository);
        Ensemble ensemble = new Ensemble("Manual Registered Ensemble", ZonedDateTime.now());
        ensemble.setId(EnsembleId.of(23));
        huddleRepository.save(ensemble);
        AdminDashboardController adminDashboardController = new AdminDashboardController(ensembleService, memberService);

        AdminRegistrationForm form = new AdminRegistrationForm(ensemble.getId());
        form.setGithubUsername("githubtwo");
        adminDashboardController.registerParticipant(form);

        assertThat(ensemble.acceptedMembers())
                .containsExactly(MemberId.of(1));
    }

    @NotNull
    private AdminDashboardController createAdminDashboardController(InMemoryEnsembleRepository huddleRepository) {
        MemberService memberService = new MemberService(new InMemoryMemberRepository());
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(huddleRepository);
        return new AdminDashboardController(ensembleService, memberService);
    }

}