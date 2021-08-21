package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.in.web.HuddleSummaryView;
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
        Member member = findMemberBy(principal);
        model.addAttribute("username", member.githubUsername());
        model.addAttribute("name", member.firstName());

        MemberRegisterForm memberRegisterForm = createRegistrationForm(member.getId());
        model.addAttribute("register", memberRegisterForm);

        List<Huddle> huddles = huddleService.allHuddles();
        List<HuddleSummaryView> huddleSummaryViews = HuddleSummaryView.from(huddles, member.getId());
        model.addAttribute("huddles", huddleSummaryViews);
        return "member-register";
    }

    private Member findMemberBy(AuthenticatedPrincipal principal) {
        Member member;
        if (principal instanceof OAuth2User oAuth2User) {
            String username = oAuth2User.getAttribute("login");
            member = memberService.findByGithubUsername(username);
        } else {
            throw new IllegalStateException("Not an OAuth2User");
        }
        return member;
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
