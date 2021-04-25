package com.jitterted.moborg.adapter.in.web;

import com.jitterted.moborg.domain.Huddle;
import com.jitterted.moborg.domain.HuddleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.ZonedDateTime;
import java.util.List;

@Controller
public class DashboardController {

  private final HuddleService huddleService;

  @Autowired
  public DashboardController(HuddleService huddleService) {
    this.huddleService = huddleService;
  }

  @GetMapping("/dashboard")
  public String dashboardView(Model model) {
    List<Huddle> huddles = huddleService.allHuddles();
    List<HuddleSummaryView> huddleSummaryViews = HuddleSummaryView.from(huddles);
    model.addAttribute("huddles", huddleSummaryViews);
    model.addAttribute("scheduleHuddleForm", new ScheduleHuddleForm());
    return "dashboard";
  }

  @GetMapping("/huddle")
  public String huddleDetailView(Model model) {
    HuddleDetailView huddleDetailView = new HuddleDetailView("Name", "04/24/2021 10am PDT", "90 minutes", "Blackjack", 4);
    model.addAttribute("huddle", huddleDetailView);
    return "huddle-detail";
  }

  @PostMapping("/schedule")
  public String scheduleHuddle(ScheduleHuddleForm scheduleHuddleForm) {
    ZonedDateTime dateTime = ZonedDateTime.now();
    huddleService.scheduleHuddle(scheduleHuddleForm.getName(), dateTime);
    return "redirect:/dashboard";
  }
}
