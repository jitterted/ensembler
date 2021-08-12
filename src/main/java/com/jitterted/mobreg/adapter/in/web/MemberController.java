package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.HuddleService;
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

    @Autowired
    public MemberController(HuddleService huddleService) {
        this.huddleService = huddleService;
    }

    @GetMapping("/member/register")
    public String showHuddlesForUser(Model model, @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        MemberRegisterForm memberRegisterForm;
        // GOAL: replace username here with a lookup in MemberRepository for the Member
        String username;
        if (principal instanceof OAuth2User oAuth2User) {
            username = oAuth2User.getAttribute("login");
            // GOAL: replace with Member.firstName
            final String displayName = oAuth2User.getAttribute("name");
            model.addAttribute("username", username); // Member.githubUsername
            model.addAttribute("name", displayName);
            memberRegisterForm = createRegistrationForm(username, displayName);
        } else {
            throw new IllegalStateException("Not an OAuth2User");
        }
        List<Huddle> huddles = huddleService.allHuddles();
        List<HuddleSummaryView> huddleSummaryViews = HuddleSummaryView.from(huddles, username);
        model.addAttribute("register", memberRegisterForm);
        model.addAttribute("huddles", huddleSummaryViews);
        return "member-register";
    }

    private MemberRegisterForm createRegistrationForm(String username, String displayName) {
        MemberRegisterForm memberRegisterForm = new MemberRegisterForm();
        memberRegisterForm.setName(displayName);
        memberRegisterForm.setUsername(username);
        // TODO: add MemberId (via lookup in MemberService)
        // TODO: remove name/username from this form
        return memberRegisterForm;
    }

    @PostMapping("/member/register")
    public String register(MemberRegisterForm memberRegisterForm) {
        HuddleId huddleId = HuddleId.of(memberRegisterForm.getHuddleId());

        huddleService.registerParticipant(huddleId,
                                          memberRegisterForm.getName(),
                                          memberRegisterForm.getUsername()
        );

        return "redirect:/member/register";
    }

}
