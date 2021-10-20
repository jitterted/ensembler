package com.jitterted.mobreg.adapter.in.web;

import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    @GetMapping("/member")
    public String memberHome() {
        return "redirect:/member/register";
    }

    @GetMapping("/user")
    public String userHome(Model model, @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        String username = ((OAuth2User) principal).getAttribute("login");
        model.addAttribute("username", username);
        return "user";
    }

}
