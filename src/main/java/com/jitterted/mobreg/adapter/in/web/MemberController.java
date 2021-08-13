package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.HuddleService;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MemberController {

    private final HuddleService huddleService;
    private final MemberService memberService;

    @Autowired
    public MemberController(HuddleService huddleService, MemberService memberService) {
        this.huddleService = huddleService;
        this.memberService = memberService;
    }

    @GetMapping("/member/register")
    public String showHuddlesForUser(Model model, @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        MemberRegisterForm memberRegisterForm;
        MemberId memberId;
        if (principal instanceof OAuth2User oAuth2User) {
            String username = oAuth2User.getAttribute("login");
            Member member = memberService.findByGithubUsername(username);
            memberId = member.getId();
            model.addAttribute("username", username); // Member.githubUsername
            model.addAttribute("name", member.firstName());
            memberRegisterForm = createRegistrationForm(memberId);
        } else {
            throw new IllegalStateException("Not an OAuth2User");
        }
        List<Huddle> huddles = huddleService.allHuddles();
        List<HuddleSummaryView> huddleSummaryViews = HuddleSummaryView.from(huddles, memberId);
        model.addAttribute("register", memberRegisterForm);
        model.addAttribute("huddles", huddleSummaryViews);
        return "member-register";
    }

    private MemberRegisterForm createRegistrationForm(MemberId memberId) {
        MemberRegisterForm memberRegisterForm = new MemberRegisterForm();
        memberRegisterForm.setMemberId(memberId.id());
        return memberRegisterForm;
    }

    @PostMapping("/member/register")
    public String register(MemberRegisterForm memberRegisterForm) {
        HuddleId huddleId = HuddleId.of(memberRegisterForm.getHuddleId());
        MemberId memberId = MemberId.of(memberRegisterForm.getMemberId());

        huddleService.registerMember(huddleId, memberId);

        return "redirect:/member/register";
    }

}
