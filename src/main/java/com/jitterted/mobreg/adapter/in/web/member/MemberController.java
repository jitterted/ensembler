package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.HuddleService;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MemberController {

    private final HuddleService huddleService;
    private final MemberLookup memberLookup;

    @Autowired
    public MemberController(HuddleService huddleService, MemberService memberService) {
        this.huddleService = huddleService;
        this.memberLookup = new MemberLookup(memberService);
    }

    @GetMapping("/member/register")
    public String showHuddlesForUser(Model model, @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        Member member = memberLookup.findMemberBy(principal);
        model.addAttribute("githubUsername", member.githubUsername());
        model.addAttribute("firstName", member.firstName());

        MemberRegisterForm memberRegisterForm = createRegistrationForm(member.getId());
        model.addAttribute("register", memberRegisterForm);

        List<Huddle> huddles = huddleService.allHuddlesByDateTimeDescending();
        List<HuddleSummaryView> huddleSummaryViews = HuddleSummaryView.from(huddles, member.getId());
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
