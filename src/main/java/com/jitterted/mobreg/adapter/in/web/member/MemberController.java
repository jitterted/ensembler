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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

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

        addEnsemblesToModel(model, member);
        return "member-register";
    }

    public void addEnsemblesToModel(Model model, Member member) {
        MemberId memberId = member.getId();

        Optional<InProgressEnsembleView> inProgressEnsemble =
                ensembleService.inProgressEnsemble(ZonedDateTime.now(), memberId)
                               .map(ensemble -> InProgressEnsembleView.from(ensemble, memberService));
        model.addAttribute("inProgressEnsembleViewOptional", inProgressEnsemble);

        List<Ensemble> upcomingEnsembles = ensembleService.allUpcomingEnsembles(ZonedDateTime.now());
        List<EnsembleSummaryView> availableEnsembleSummaryViews = EnsembleSummaryView.from(upcomingEnsembles, memberId, memberService, EnsembleSortOrder.ASCENDING_ORDER);
        model.addAttribute("upcomingEnsembles", availableEnsembleSummaryViews);

        List<Ensemble> pastEnsembles = ensembleService.allInThePastFor(memberId, ZonedDateTime.now());
        List<EnsembleSummaryView> pastEnsembleSummaryViews = EnsembleSummaryView.from(pastEnsembles, memberId, memberService, EnsembleSortOrder.DESCENDING_ORDER);
        model.addAttribute("pastEnsembles", pastEnsembleSummaryViews);
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
