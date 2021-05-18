package com.jitterted.moborg.adapter.in.web;

import com.jitterted.moborg.domain.Huddle;
import com.jitterted.moborg.domain.HuddleService;
import com.jitterted.moborg.domain.InMemoryHuddleRepository;
import com.jitterted.moborg.domain.Participant;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

public class DashboardHuddleViewTest {

    @Test
    public void detailViewOfExistingHuddleByItsIdIsReturned() throws Exception {
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = new HuddleService(huddleRepository);
        Huddle savedHuddle = huddleRepository.save(new Huddle("Huddle #1", ZonedDateTime.now()));
        DashboardController dashboardController = new DashboardController(huddleService);

        Model model = new ConcurrentModel();
        dashboardController.huddleDetailView(model, 0L);

        HuddleDetailView huddle = (HuddleDetailView) model.getAttribute("huddle");

        assertThat(huddle.name())
                .isEqualTo(savedHuddle.name());
    }

    @Test
    public void detailViewOfExistingHuddleWithOneParticipantReturnsHuddleWithParticipantView() throws Exception {
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = new HuddleService(huddleRepository);
        Huddle huddle = new Huddle("Huddle #1", ZonedDateTime.now());
        huddle.register(new Participant("name", "github", null, null, false));
        huddleRepository.save(huddle);
        DashboardController dashboardController = new DashboardController(huddleService);

        Model model = new ConcurrentModel();
        dashboardController.huddleDetailView(model, 0L);

        HuddleDetailView huddleView = (HuddleDetailView) model.getAttribute("huddle");

        assertThat(huddleView.participantViews())
                .hasSize(1);
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
