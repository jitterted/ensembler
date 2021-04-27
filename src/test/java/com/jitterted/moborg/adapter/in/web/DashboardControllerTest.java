package com.jitterted.moborg.adapter.in.web;

import com.jitterted.moborg.domain.Huddle;
import com.jitterted.moborg.domain.HuddleService;
import com.jitterted.moborg.domain.InMemoryHuddleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DashboardControllerTest {

  @Test
  public void givenOneHuddleResultsInHuddleInViewModel() throws Exception {
    InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
    HuddleService huddleService = new HuddleService(huddleRepository);
    huddleRepository.save(new Huddle("Name", ZonedDateTime.now()));
    DashboardController dashboardController = new DashboardController(huddleService);

    Model model = new ConcurrentModel();
    dashboardController.dashboardView(model);

    List<HuddleSummaryView> huddleSummaryViews = (List<HuddleSummaryView>) model.getAttribute("huddles");
    assertThat(huddleSummaryViews)
        .hasSize(1);
  }

  @Test
  public void scheduleNewHuddleResultsInHuddleCreatedInRepository() throws Exception {
    InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
    HuddleService huddleService = new HuddleService(huddleRepository);
    DashboardController dashboardController = new DashboardController(huddleService);

    String pageName = dashboardController.scheduleHuddle(new ScheduleHuddleForm("Name", "2021-04-30", "09:00 AM"));

    assertThat(pageName)
        .isEqualTo("redirect:/dashboard");
    assertThat(huddleRepository.findAll())
        .hasSize(1);
  }

}