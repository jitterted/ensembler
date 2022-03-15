package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.in.web.GitHubUsernamePrincipalExtractor;
import com.jitterted.mobreg.application.port.InviteRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
public class InvitationController {

    private final MemberRepository memberRepository;
    private final InviteRepository inviteRepository;

    @Autowired
    public InvitationController(MemberRepository memberRepository, InviteRepository inviteRepository) {
        this.memberRepository = memberRepository;
        this.inviteRepository = inviteRepository;
    }

    @GetMapping("/invite")
    public String processInvitation(String token, @AuthenticationPrincipal AuthenticatedPrincipal authenticatedPrincipal) {
        String githubUsername = GitHubUsernamePrincipalExtractor.usernameFrom(authenticatedPrincipal);
        if (inviteRepository.existsByTokenAndGithubUsernameAndWasUsedFalse(token, githubUsername)) {
            Member member = new Member("", githubUsername, "ROLE_MEMBER", "ROLE_USER");
            memberRepository.save(member);
            inviteRepository.markInviteAsUsed(token, LocalDateTime.now());
            return "redirect:/member/profile";
        }
        return "invite-invalid";
    }

}
