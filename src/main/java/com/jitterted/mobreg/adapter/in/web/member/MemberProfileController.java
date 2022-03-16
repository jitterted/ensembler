package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.ZoneId;
import java.util.List;

@Controller
public class MemberProfileController {

    private final MemberLookup memberLookup;
    private final MemberService memberService;

    @Autowired
    public MemberProfileController(MemberService memberService) {
        this.memberLookup = new MemberLookup(memberService);
        this.memberService = memberService;
    }

    @GetMapping("/member/profile")
    public String prepareMemberProfileForm(Model model,
                                           @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        Member member = memberLookup.findMemberBy(principal);
        model.addAttribute("githubUsername", member.githubUsername());
        model.addAttribute("firstName", member.firstName());

        model.addAttribute("profile", new MemberProfileForm(member));
        return "member-profile";
    }

    @PostMapping("/member/profile")
    public String updateProfileFromForm(@Valid MemberProfileForm memberProfileForm,
                                        @AuthenticationPrincipal AuthenticatedPrincipal principal,
                                        RedirectAttributes redirectAttrs) {
        Member member = memberLookup.findMemberBy(principal);
        memberService.changeFirstName(member, memberProfileForm.getFirstName());
        memberService.changeEmail(member, memberProfileForm.getEmail());
        memberService.changeTimeZone(member, memberProfileForm.getTimeZone());
        redirectAttrs.addFlashAttribute("updated", true);
        return "redirect:/member/profile";
    }

    @ModelAttribute("allTimeZones")
    public List<String> allTimeZonesForDropdown() {
        return ZoneId.getAvailableZoneIds().stream().sorted().toList();
    }

}
