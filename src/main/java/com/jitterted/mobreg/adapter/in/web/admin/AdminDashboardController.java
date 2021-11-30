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
        List<Ensemble> ensembles = ensembleService.allHuddlesByDateTimeDescending();
        List<HuddleSummaryView> huddleSummaryViews = HuddleSummaryView.from(ensembles);
        model.addAttribute("ensembles", huddleSummaryViews);
        model.addAttribute("scheduleHuddleForm", new ScheduleHuddleForm());
        return "dashboard";
    }

    @GetMapping("/huddle/{huddleId}")
    public String huddleDetailView(Model model, @PathVariable("huddleId") Long huddleId) {
        Ensemble ensemble = ensembleService.findById(EnsembleId.of(huddleId))
                                           .orElseThrow(() -> {
                                         throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                                     });

        HuddleDetailView huddleDetailView = HuddleDetailView.from(ensemble, memberService);
        model.addAttribute("ensemble", huddleDetailView);
        model.addAttribute("scheduleHuddleForm", ScheduleHuddleForm.from(ensemble));
        model.addAttribute("completeHuddle", new CompleteHuddleForm(""));
        model.addAttribute("registration", new AdminRegistrationForm(ensemble.getId()));

        return "huddle-detail";
    }

    @PostMapping("/huddle/{huddleId}")
    public String changeHuddle(ScheduleHuddleForm scheduleHuddleForm, @PathVariable("huddleId") Long id) {
        EnsembleId ensembleId = EnsembleId.of(id);
        ensembleService.changeNameDateTimeTo(ensembleId, scheduleHuddleForm.getName(), scheduleHuddleForm.getDateTimeInUtc());
        return "redirect:/admin/huddle/" + id;
    }

    @PostMapping("/schedule")
    public String scheduleHuddle(ScheduleHuddleForm scheduleHuddleForm) {
        if (scheduleHuddleForm.getZoomMeetingLink().isBlank()) {
            ensembleService.scheduleHuddle(scheduleHuddleForm.getName(),
                                           scheduleHuddleForm.getDateTimeInUtc());
        } else {
            ensembleService.scheduleHuddle(scheduleHuddleForm.getName(),
                                           URI.create(scheduleHuddleForm.getZoomMeetingLink()),
                                           scheduleHuddleForm.getDateTimeInUtc());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/notify/{huddleId}")
    public String notifyHuddleScheduled(@PathVariable("huddleId") Long huddleId) {
        Ensemble ensemble = ensembleService.findById(EnsembleId.of(huddleId))
                                           .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                });
        ensembleService.triggerHuddleOpenedNotification(ensemble);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/register")
    public String registerParticipant(AdminRegistrationForm adminRegistrationForm) {
        EnsembleId ensembleId = EnsembleId.of(adminRegistrationForm.getEnsembleId());

        Member member = memberService.findByGithubUsername(adminRegistrationForm.getGithubUsername());

        ensembleService.registerMember(ensembleId, member.getId());

        return redirectToDetailViewFor(ensembleId);
    }

    @PostMapping("/huddle/{huddleId}/complete")
    public String completeHuddle(@PathVariable("huddleId") long id, CompleteHuddleForm completeHuddleForm) {
        EnsembleId ensembleId = EnsembleId.of(id);
        ensembleService.completeWith(ensembleId, completeHuddleForm.recordingLink());

        return redirectToDetailViewFor(ensembleId);
    }

    private String redirectToDetailViewFor(EnsembleId ensembleId) {
        return "redirect:/admin/huddle/" + ensembleId.id();
    }

}
