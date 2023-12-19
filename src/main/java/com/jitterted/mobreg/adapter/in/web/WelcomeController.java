package com.jitterted.mobreg.adapter.in.web;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    @GetMapping("/member")
    public String memberHome(@CurrentSecurityContext SecurityContext context) {
        if (context.getAuthentication().getName().equalsIgnoreCase("anonymousUser")) {
            throw new AccessDeniedException("Access Denied for Anonymous User");
        }
        return "redirect:/member/register";
    }

    @GetMapping("/user")
    public String userHome(Model model, @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        String username = GitHubUsernamePrincipalExtractor.usernameFrom(principal);
        model.addAttribute("username", username);
        return "user";
    }

}
