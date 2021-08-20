package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleService;
import com.jitterted.mobreg.domain.InMemoryHuddleRepository;
import com.jitterted.mobreg.domain.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberFactory;
import com.jitterted.mobreg.domain.MemberService;
import com.jitterted.mobreg.domain.OAuth2UserFactory;
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
        MemberService memberService = new MemberService(new InMemoryMemberRepository());
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = new HuddleService(huddleRepository);
        AdminDashboardController adminDashboardController = new AdminDashboardController(huddleService, memberService);

        String pageName = adminDashboardController.scheduleHuddle(new ScheduleHuddleForm("Name", "https://zoom.us/j/123456?pwd=12345", "2021-04-30", "09:00"));

        assertThat(pageName)
                .isEqualTo("redirect:/admin/dashboard");
        assertThat(huddleRepository.findAll())
                .hasSize(1);
    }

}