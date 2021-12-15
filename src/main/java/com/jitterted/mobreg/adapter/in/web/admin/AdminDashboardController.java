package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final EnsembleService ensembleService;
    private final MemberService memberService;

    @Autowired
    public AdminDashboardController(EnsembleService ensembleService,
                                    MemberService memberService) {
        this.ensembleService = ensembleService;
        this.memberService = memberService;
    }

    @GetMapping("/dashboard")
    public String dashboardView(Model model, @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        if (principal instanceof OAuth2User oAuth2User) {
            String username = oAuth2User.getAttribute("login");
            Member member = memberService.findByGithubUsername(username);
            model.addAttribute("username", username); // Member.githubUsername
            model.addAttribute("name", member.firstName());
            model.addAttribute("github_id", oAuth2User.getAttribute("id"));
        } else {
            throw new IllegalStateException("Not an OAuth2User");
        }
        List<Ensemble> ensembles = ensembleService.allEnsemblesByDateTimeDescending();
        List<EnsembleSummaryView> ensembleSummaryViews = EnsembleSummaryView.from(ensembles);
        model.addAttribute("ensembles", ensembleSummaryViews);
        model.addAttribute("scheduleEnsembleForm", new ScheduleEnsembleForm());
        return "dashboard";
    }

    @GetMapping("/ensemble/{ensembleId}")
    public String ensembleDetailView(Model model, @PathVariable("ensembleId") Long ensembleId) {
        Ensemble ensemble = ensembleService.findById(EnsembleId.of(ensembleId))
                                           .orElseThrow(() -> {
                                               throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                                           });

        EnsembleDetailView ensembleDetailView = EnsembleDetailView.from(ensemble, memberService);
        model.addAttribute("ensemble", ensembleDetailView);
        model.addAttribute("scheduleEnsembleForm", ScheduleEnsembleForm.from(ensemble));
        model.addAttribute("completeEnsemble", new CompleteEnsembleForm(""));
        model.addAttribute("registration", new AdminRegistrationForm(ensemble.getId()));

        return "ensemble-detail";
    }

    @PostMapping("/ensemble/{ensembleId}")
    public String changeEnsemble(ScheduleEnsembleForm scheduleEnsembleForm, @PathVariable("ensembleId") Long id) {
        EnsembleId ensembleId = EnsembleId.of(id);
        ensembleService.changeNameDateTimeTo(ensembleId,
                                             scheduleEnsembleForm.getName(),
                                             scheduleEnsembleForm.getDateTimeInUtc());
        return redirectToDetailViewFor(ensembleId);
    }

    @PostMapping("/schedule")
    public String scheduleEnsemble(ScheduleEnsembleForm scheduleEnsembleForm) {
        if (scheduleEnsembleForm.getZoomMeetingLink().isBlank()) {
            ensembleService.scheduleEnsembleWithVideoConference(scheduleEnsembleForm.getName(),
                                                                scheduleEnsembleForm.getDateTimeInUtc());
        } else {
            ensembleService.scheduleEnsemble(scheduleEnsembleForm.getName(),
                                             URI.create(scheduleEnsembleForm.getZoomMeetingLink()),
                                             scheduleEnsembleForm.getDateTimeInUtc());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/notify/{ensembleId}")
    public String notifyEnsembleScheduled(@PathVariable("ensembleId") Long ensembleId) {
        Ensemble ensemble = ensembleService.findById(EnsembleId.of(ensembleId))
                                           .orElseThrow(() -> {
                                               throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                                           });
        ensembleService.triggerEnsembleScheduledNotification(ensemble);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/register")
    public String registerParticipant(AdminRegistrationForm adminRegistrationForm) {
        EnsembleId ensembleId = EnsembleId.of(adminRegistrationForm.getEnsembleId());

        Member member = memberService.findByGithubUsername(adminRegistrationForm.getGithubUsername());

        ensembleService.registerMember(ensembleId, member.getId());

        return redirectToDetailViewFor(ensembleId);
    }

    @PostMapping("/ensemble/{ensembleId}/complete")
    public String completeEnsemble(@PathVariable("ensembleId") long id, CompleteEnsembleForm completeEnsembleForm) {
        EnsembleId ensembleId = EnsembleId.of(id);
        ensembleService.completeWith(ensembleId, completeEnsembleForm.recordingLink());

        return redirectToDetailViewFor(ensembleId);
    }

    private String redirectToDetailViewFor(EnsembleId ensembleId) {
        return "redirect:/admin/ensemble/" + ensembleId.id();
    }

}
