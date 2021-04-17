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

    List<Huddle> huddles = (List<Huddle>) model.getAttribute("huddles");

    assertThat(huddles)
        .hasSize(1);
  }

}