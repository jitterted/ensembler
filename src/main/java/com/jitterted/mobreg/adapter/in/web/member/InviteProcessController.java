package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.in.web.GitHubUsernamePrincipalExtractor;
import com.jitterted.mobreg.application.port.InviteRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Collection;

@Controller
public class InviteProcessController {

    private final MemberRepository memberRepository;
    private final InviteRepository inviteRepository;

    @Autowired
    public InviteProcessController(MemberRepository memberRepository, InviteRepository inviteRepository) {
        this.memberRepository = memberRepository;
        this.inviteRepository = inviteRepository;
    }

    @GetMapping("/invite")
    public String processInvitation(@RequestParam(value = "invite_token", defaultValue = "") String token,
                                    @AuthenticationPrincipal AuthenticatedPrincipal authenticatedPrincipal,
                                    Model model) {
        String githubUsername = GitHubUsernamePrincipalExtractor.usernameFrom(authenticatedPrincipal);
        if (memberRepository.findByGithubUsername(githubUsername.toLowerCase())
                            .stream()
                            .map(Member::roles)
                            .flatMap(Collection::stream)
                            .anyMatch(s -> s.equals("ROLE_MEMBER"))) {
            return "redirect:/member/register";
        }
        if (inviteRepository.existsByTokenAndGithubUsernameAndWasUsedFalse(token, githubUsername.toLowerCase())) {
            Member member = new Member("", githubUsername.toLowerCase(), "ROLE_MEMBER", "ROLE_USER");
            memberRepository.save(member);
            inviteRepository.markInviteAsUsed(token, LocalDateTime.now());
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
            return "redirect:/member/profile";
        }
        model.addAttribute("username", githubUsername);
        return "invite-invalid";
    }

}
