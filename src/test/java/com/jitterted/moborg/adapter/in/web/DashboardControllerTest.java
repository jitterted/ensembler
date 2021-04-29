package com.jitterted.moborg.adapter.in.web;

import com.jitterted.moborg.domain.Huddle;
import com.jitterted.moborg.domain.HuddleService;
import com.jitterted.moborg.domain.InMemoryHuddleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SuppressWarnings("ConstantConditions")
class DashboardControllerTest {

  @Test
  public void givenOneHuddleResultsInHuddleInViewModel() throws Exception {
    InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
    HuddleService huddleService = new HuddleService(huddleRepository);
    huddleRepository.save(new Huddle("Name", ZonedDateTime.now()));
    DashboardController dashboardController = new DashboardController(huddleService);

    Model model = new ConcurrentModel();
    dashboardController.dashboardView(model, mock(OAuth2User.class));

    List<HuddleSummaryView> huddleSummaryViews = (List<HuddleSummaryView>) model.getAttribute("huddles");
    assertThat(huddleSummaryViews)
        .hasSize(1);
  }

  @Test
  public void scheduleNewHuddleResultsInHuddleCreatedInRepository() throws Exception {
    InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
    HuddleService huddleService = new HuddleService(huddleRepository);
    DashboardController dashboardController = new DashboardController(huddleService);

    String pageName = dashboardController.scheduleHuddle(new ScheduleHuddleForm("Name", "2021-04-30", "09:00"));

    assertThat(pageName)
        .isEqualTo("redirect:/dashboard");
    assertThat(huddleRepository.findAll())
        .hasSize(1);
  }

  @Test
  public void detailViewOfExistingHuddleByItsIdIsReturned() throws Exception {
    InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
    Huddle savedHuddle = huddleRepository.save(new Huddle("Huddle #1", ZonedDateTime.now()));
    HuddleService huddleService = new HuddleService(huddleRepository);
    DashboardController dashboardController = new DashboardController(huddleService);

    Model model = new ConcurrentModel();
    dashboardController.huddleDetailView(model, 0L);

    HuddleDetailView huddle = (HuddleDetailView) model.getAttribute("huddle");

    assertThat(huddle.name())
        .isEqualTo(savedHuddle.name());
  }

  @Test
  public void detailViewOfNonExistentHuddleReturns404NotFound() throws Exception {
    InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
    HuddleService huddleService = new HuddleService(huddleRepository);
    DashboardController dashboardController = new DashboardController(huddleService);
    Model model = new ConcurrentModel();

    assertThatThrownBy(() -> {
      dashboardController.huddleDetailView(model, 0L);
    }).isInstanceOf(ResponseStatusException.class)
      .extracting("status")
      .isEqualTo(HttpStatus.NOT_FOUND);
  }

}