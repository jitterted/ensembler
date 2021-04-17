package com.jitterted.moborg.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DashboardControllerTest {

  @Test
  public void givenOneHuddleResultsInHuddlePutIntoModel() throws Exception {
    DashboardController dashboardController = new DashboardController();

    Model model = new ConcurrentModel();
    dashboardController.dashboardView(model);

    List<HuddleSummaryView> huddleSummaryViews = (List<HuddleSummaryView>) model.getAttribute("huddles");

    assertThat(huddleSummaryViews)
        .hasSize(1);
  }

}