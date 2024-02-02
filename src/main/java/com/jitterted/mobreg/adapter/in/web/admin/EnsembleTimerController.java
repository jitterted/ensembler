package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/timer-view/")
public class EnsembleTimerController {
    private final EnsembleTimerHolder ensembleTimerHolder;

    @Autowired
    public EnsembleTimerController(EnsembleTimerHolder ensembleTimerHolder) {
        this.ensembleTimerHolder = ensembleTimerHolder;
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
        model.addAttribute("participantNames", List.of("names go here"));
        return "ensemble-timer";
    }
}
