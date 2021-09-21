package com.jitterted.mobreg.adapter.in.web.member;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberProfileController {

    @GetMapping("/member/profile")
    public String prepareMemberProfileForm(Model model) {
        model.addAttribute("githubUsername", "username");
        model.addAttribute("firstName", "firstname");
        model.addAttribute("memberProfileForm", new MemberProfileForm());
        return "member-profile";
    }

}
