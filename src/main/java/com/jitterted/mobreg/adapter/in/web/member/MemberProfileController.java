package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/member/profile")
public class MemberProfileController {

    private final MemberLookup memberLookup;
    private final MemberService memberService;

    @Autowired
    public MemberProfileController(MemberService memberService) {
        this.memberLookup = new MemberLookup(memberService);
        this.memberService = memberService;
    }

    @GetMapping
    public String prepareMemberProfileForm(Model model, @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        Member member = memberLookup.findMemberBy(principal);
        model.addAttribute("githubUsername", member.githubUsername());
        model.addAttribute("firstName", member.firstName());

        model.addAttribute("profile", new MemberProfileForm(member));
        return "member-profile";
    }

    @PostMapping
    public String updateProfileFromForm(@Valid MemberProfileForm memberProfileForm,
                                        @AuthenticationPrincipal AuthenticatedPrincipal principal,
                                        RedirectAttributes redirectAttrs) {
        Member member = memberLookup.findMemberBy(principal);
        memberService.changeEmail(member, memberProfileForm.getEmail());
        redirectAttrs.addFlashAttribute("updated", true);
        return "redirect:/member/profile";
    }

}
