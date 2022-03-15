package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.application.port.InviteRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InvitationController {

    private MemberRepository memberRepository;
    private InviteRepository inviteRepository;

    public InvitationController(MemberRepository memberRepository, InviteRepository inviteRepository) {
        this.memberRepository = memberRepository;
        this.inviteRepository = inviteRepository;
    }

    @GetMapping("/invite")
    public String processInvitation(String token, AuthenticatedPrincipal authenticatedPrincipal) {
        return "redirect:/member/profile";
    }

}
