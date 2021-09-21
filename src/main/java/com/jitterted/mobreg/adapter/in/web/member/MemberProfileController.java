package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberProfileController {

    private final MemberLookup memberLookup;

    @Autowired
    public MemberProfileController(MemberService memberService) {
        this.memberLookup = new MemberLookup(memberService);
    }

    @GetMapping("/member/profile")
    public String prepareMemberProfileForm(Model model, @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        Member member = memberLookup.findMemberBy(principal);
        model.addAttribute("githubUsername", member.githubUsername());
        model.addAttribute("firstName", member.firstName());

        model.addAttribute("profile", new MemberProfileForm(member));
        return "member-profile";
    }

}
