package com.jitterted.mobreg.adapter.in.web.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NewMemberInvitationController {

    @GetMapping("/invite")
    public String processInvitation() {
        return "redirect:/";
    }

}
