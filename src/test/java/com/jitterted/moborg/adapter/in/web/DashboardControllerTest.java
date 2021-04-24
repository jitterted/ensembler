package com.jitterted.moborg.adapter.in.web;

import com.jitterted.moborg.domain.HuddleService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DashboardControllerTest {

  @Test
  public void givenOneHuddleResultsInHuddlePutIntoModel() throws Exception {
    HuddleService huddleService = new HuddleService();
    DashboardController dashboardController = new DashboardController(huddleService);

    Model model = new ConcurrentModel();
    dashboardController.dashboardView(model);

    List<HuddleSummaryView> huddleSummaryViews = (List<HuddleSummaryView>) model.getAttribute("huddles");

    assertThat(huddleSummaryViews)
        .hasSize(1);
  }

}