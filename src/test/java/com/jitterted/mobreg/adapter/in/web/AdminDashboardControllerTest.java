package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleService;
import com.jitterted.mobreg.domain.InMemoryHuddleRepository;
import com.jitterted.mobreg.domain.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SuppressWarnings({"ConstantConditions", "unchecked"})
class AdminDashboardControllerTest {

    @Test
    public void givenOneHuddleResultsInHuddleInViewModel() throws Exception {
        MemberService memberService = new MemberService(new InMemoryMemberRepository());
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = new HuddleService(huddleRepository);
        huddleRepository.save(new Huddle("Name", ZonedDateTime.now()));
        AdminDashboardController adminDashboardController = new AdminDashboardController(huddleService, memberService);

        Model model = new ConcurrentModel();
        adminDashboardController.dashboardView(model, mock(AuthenticatedPrincipal.class));

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

        String pageName = adminDashboardController.scheduleHuddle(new ScheduleHuddleForm("Name", "2021-04-30", "09:00"));

        assertThat(pageName)
                .isEqualTo("redirect:/admin/dashboard");
        assertThat(huddleRepository.findAll())
                .hasSize(1);
    }

}