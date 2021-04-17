package com.jitterted.moborg.adapter.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

  @GetMapping("/dashboard")
  public String dashboardView(Model model) {
    model.addAttribute("huddles", new Huddles("test"));
    return "dashboard";
  }

}
