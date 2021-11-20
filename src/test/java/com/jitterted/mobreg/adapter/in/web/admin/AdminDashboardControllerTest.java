package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.HuddleService;
import com.jitterted.mobreg.domain.HuddleServiceFactory;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberFactory;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberService;
import com.jitterted.mobreg.domain.OAuth2UserFactory;
import com.jitterted.mobreg.domain.ZonedDateTimeFactory;
import com.jitterted.mobreg.domain.port.InMemoryHuddleRepository;
import com.jitterted.mobreg.domain.port.InMemoryMemberRepository;
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
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(huddleRepository);
        huddleRepository.save(new Huddle("Name", ZonedDateTime.now()));
        AdminDashboardController adminDashboardController = new AdminDashboardController(huddleService, memberService);

        Model model = new ConcurrentModel();
        adminDashboardController.dashboardView(model, OAuth2UserFactory.createOAuth2UserWithMemberRole("tedyoung", "ROLE_MEMBER"));

        List<HuddleSummaryView> huddleSummaryViews = (List<HuddleSummaryView>) model.getAttribute("huddles");
        assertThat(huddleSummaryViews)
                .hasSize(1);
    }

    @Test
    public void scheduleNewHuddleResultsInHuddleCreatedInRepository() throws Exception {
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
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
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        Huddle huddle = new Huddle("Old Name", ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 30, 9));
        huddleRepository.save(huddle);
        AdminDashboardController adminDashboardController = createAdminDashboardController(huddleRepository);

        ScheduleHuddleForm scheduleHuddleForm = new ScheduleHuddleForm("New Name", null, "2021-12-01", "10:00", "America/Los_Angeles");
        HuddleId huddleId = huddle.getId();
        String pageName = adminDashboardController.changeHuddle(scheduleHuddleForm, huddleId.id());

        assertThat(pageName)
                .isEqualTo("redirect:/admin/huddle/" + huddleId.id());
        Huddle expectedHuddle = new Huddle("New Name", ZonedDateTime.of(2021, 12, 1, 10, 0, 0, 0, ZoneId.of("America/Los_Angeles")).withZoneSameInstant(ZoneOffset.UTC));
        expectedHuddle.setId(huddleId);
        assertThat(huddleRepository.findById(huddleId).get())
                .usingRecursiveComparison()
                .isEqualTo(expectedHuddle);
    }


    @Test
    public void completeHuddleCompletesTheHuddleWithRecordingLinkAndRedirects() throws Exception {
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        Huddle huddle = new Huddle("to be completed", ZonedDateTime.now());
        huddle.setId(HuddleId.of(19));
        huddleRepository.save(huddle);
        AdminDashboardController adminDashboardController = createAdminDashboardController(huddleRepository);

        String pageName = adminDashboardController.completeHuddle(19, new CompleteHuddleForm("https://recording.link/19"));

        assertThat(pageName)
                .isEqualTo("redirect:/admin/huddle/19");

        assertThat(huddle.isCompleted())
                .isTrue();
        assertThat(huddle.recordingLink().toString())
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
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(huddleRepository, memberRepository);
        Huddle huddle = new Huddle("Manual Registered Huddle", ZonedDateTime.now());
        huddle.setId(HuddleId.of(23));
        huddleRepository.save(huddle);
        AdminDashboardController adminDashboardController = new AdminDashboardController(huddleService, memberService);

        AdminRegistrationForm form = new AdminRegistrationForm(huddle.getId());
        form.setGithubUsername("githubtwo");
        adminDashboardController.registerParticipant(form);

        assertThat(huddle.registeredMembers())
                .containsExactly(MemberId.of(1));
    }

    @NotNull
    private AdminDashboardController createAdminDashboardController(InMemoryHuddleRepository huddleRepository) {
        MemberService memberService = new MemberService(new InMemoryMemberRepository());
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(huddleRepository);
        return new AdminDashboardController(huddleService, memberService);
    }

}