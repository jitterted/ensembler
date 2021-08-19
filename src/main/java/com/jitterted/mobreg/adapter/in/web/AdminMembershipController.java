package com.jitterted.mobreg.adapter.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminMembershipController {

    @GetMapping("/admin/members")
    public String membersView() {
        return "members";
    }
}
