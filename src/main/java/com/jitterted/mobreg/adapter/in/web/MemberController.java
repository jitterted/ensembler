package com.jitterted.mobreg.adapter.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class MemberController {

    @GetMapping("/member/register")
    public String memberRegister(Model model) {
        List<HuddleSummaryView> huddleSummaryViews = Collections.emptyList();
        model.addAttribute("huddles", huddleSummaryViews);
        model.addAttribute("scheduleHuddleForm", new ScheduleHuddleForm());
        return "dashboard";
    }
}
