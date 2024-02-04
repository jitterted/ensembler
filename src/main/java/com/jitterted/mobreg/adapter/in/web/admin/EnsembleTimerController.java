package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/timer-view/")
public class EnsembleTimerController {
    private final EnsembleTimerHolder ensembleTimerHolder;
    private final MemberRepository memberRepository;

    @Autowired
    public EnsembleTimerController(EnsembleTimerHolder ensembleTimerHolder, MemberRepository memberRepository) {
        this.ensembleTimerHolder = ensembleTimerHolder;
        this.memberRepository = memberRepository;
    }

    @PostMapping("{ensembleId}")
    public String gotoTimerView(@PathVariable("ensembleId") Long id) {
        ensembleTimerHolder.timerFor(EnsembleId.of(id));
        return "redirect:/admin/timer-view/" + id;
    }

    @GetMapping("{ensembleId}")
    public String viewTimer(@PathVariable("ensembleId") Long id, Model model) {
        EnsembleTimer ensembleTimer = ensembleTimerHolder.timerFor(EnsembleId.of(id));
        model.addAttribute("ensembleName", ensembleTimer.ensembleName());
        model.addAttribute("participantNames", firstNamesOfParticipantsIn(ensembleTimer));
        return "ensemble-timer";
    }

    List<String> firstNamesOfParticipantsIn(EnsembleTimer ensembleTimer) {
        return ensembleTimer.participants()
                            .map(memberRepository::findById)
                            .flatMap(Optional::stream)
                            .map(Member::firstName)
                            .toList();
    }
}
