package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.HuddleService;
import com.jitterted.mobreg.domain.InMemoryHuddleRepository;
import com.jitterted.mobreg.domain.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberFactory;
import com.jitterted.mobreg.domain.MemberService;
import com.jitterted.mobreg.domain.OAuth2UserFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

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
        HuddleService huddleService = new HuddleService(huddleRepository);
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

        String pageName = adminDashboardController.scheduleHuddle(new ScheduleHuddleForm("Name", "https://zoom.us/j/123456?pwd=12345", "2021-04-30", "09:00"));

        assertThat(pageName)
                .isEqualTo("redirect:/admin/dashboard");
        assertThat(huddleRepository.findAll())
                .hasSize(1);
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

    @NotNull
    private AdminDashboardController createAdminDashboardController(InMemoryHuddleRepository huddleRepository) {
        MemberService memberService = new MemberService(new InMemoryMemberRepository());
        HuddleService huddleService = new HuddleService(huddleRepository);
        return new AdminDashboardController(huddleService, memberService);
    }

}