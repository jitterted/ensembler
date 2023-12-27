package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MemberController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemberController.class);

    private final EnsembleService ensembleService;
    private final MemberLookup memberLookup;
    private final MemberService memberService;

    public MemberController(EnsembleService ensembleService, MemberService memberService) {
        this.ensembleService = ensembleService;
        this.memberLookup = new MemberLookup(memberService);
        this.memberService = memberService;
    }

    @GetMapping("/member/register")
    public String showEnsemblesForUser(Model model,
                                       @AuthenticationPrincipal AuthenticatedPrincipal principal,
                                       @CurrentSecurityContext SecurityContext context) {
        if (context.getAuthentication().getName().equalsIgnoreCase("anonymousUser")) {
            throw new AccessDeniedException("Access Denied for Anonymous User");
        }

        Member member = memberLookup.findMemberBy(principal);
        model.addAttribute("githubUsername", member.githubUsername());
        model.addAttribute("firstName", member.firstName());

        MemberRegisterForm memberRegisterForm = createRegistrationForm(member.getId());
        model.addAttribute("register", memberRegisterForm);

        List<EnsembleSummaryView> ensembleSummaryViews = summaryViewsFor(member.getId());
        model.addAttribute("ensembles", ensembleSummaryViews);
        return "member-register";
    }

    public List<EnsembleSummaryView> summaryViewsFor(MemberId memberId) {
        List<Ensemble> ensembles = ensembleService.allEnsemblesByDateTimeDescending();
        List<EnsembleSummaryView> ensembleSummaryViews = EnsembleSummaryView.from(ensembles, memberId, memberService);
        return ensembleSummaryViews;
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

        ensembleService.joinAsParticipant(ensembleId, memberId);

        return "redirect:/member/register";
    }

    @PostMapping("/member/join-as-spectator")
    public String joinAsSpectator(MemberRegisterForm memberRegisterForm) {
        EnsembleId ensembleId = EnsembleId.of(memberRegisterForm.getEnsembleId());
        MemberId memberId = MemberId.of(memberRegisterForm.getMemberId());

        ensembleService.joinAsSpectator(ensembleId, memberId);

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
