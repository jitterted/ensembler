package com.jitterted.moborg.adapter.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

  @GetMapping("/dashboard")
  public String dashboardView(Model model) {
    HuddleSummaryView huddleSummaryView = new HuddleSummaryView("Name", "Date/Time", 1);
    model.addAttribute("huddles", List.of(huddleSummaryView));
    return "dashboard";
  }

}
