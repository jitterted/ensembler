package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
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

    private final EnsembleService ensembleService;
    private final MemberLookup memberLookup;

    @Autowired
    public MemberController(EnsembleService ensembleService, MemberService memberService) {
        this.ensembleService = ensembleService;
        this.memberLookup = new MemberLookup(memberService);
    }

    @GetMapping("/member/register")
    public String showEnsemblesForUser(Model model, @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        Member member = memberLookup.findMemberBy(principal);
        model.addAttribute("githubUsername", member.githubUsername());
        model.addAttribute("firstName", member.firstName());

        MemberRegisterForm memberRegisterForm = createRegistrationForm(member.getId());
        model.addAttribute("register", memberRegisterForm);

        List<Ensemble> ensembles = ensembleService.allEnsemblesByDateTimeDescending();
        List<EnsembleSummaryView> ensembleSummaryViews = EnsembleSummaryView.from(ensembles, member.getId());
        model.addAttribute("ensembles", ensembleSummaryViews);
        return "member-register";
    }

    private MemberRegisterForm createRegistrationForm(MemberId memberId) {
        MemberRegisterForm memberRegisterForm = new MemberRegisterForm();
        memberRegisterForm.setMemberId(memberId.id());
        return memberRegisterForm;
    }

    @PostMapping("/member/accept")
    public String accept(MemberRegisterForm memberRegisterForm) {
        EnsembleId ensembleId = EnsembleId.of(memberRegisterForm.getEnsembleId());
        MemberId memberId = MemberId.of(memberRegisterForm.getMemberId());

        ensembleService.registerMember(ensembleId, memberId);

        return "redirect:/member/register";
    }

    @PostMapping("/member/decline")
    public String decline(MemberRegisterForm memberRegisterForm) {
        EnsembleId ensembleId = EnsembleId.of(memberRegisterForm.getEnsembleId());
        MemberId memberId = MemberId.of(memberRegisterForm.getMemberId());

        ensembleService.declineMember(ensembleId, memberId);

        return "redirect:/member/register";
    }

}
