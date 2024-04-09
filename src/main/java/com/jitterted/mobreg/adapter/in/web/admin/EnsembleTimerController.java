package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;

@Controller
@RequestMapping("/admin")
public class EnsembleTimerController {
    private final EnsembleTimerHolder ensembleTimerHolder;

    @Autowired
    public EnsembleTimerController(EnsembleTimerHolder ensembleTimerHolder) {
        this.ensembleTimerHolder = ensembleTimerHolder;
    }

    @GetMapping("/timer-view/{ensembleId}")
    public String viewTimer(@PathVariable("ensembleId") Long id, Model model) {
        EnsembleTimer ensembleTimer = ensembleTimerHolder.timerFor(EnsembleId.of(id));
        model.addAttribute("ensembleId", id);
        model.addAttribute("ensembleName", ensembleTimer.ensembleName());
        model.addAllAttributes(ParticipantsTransformer.participantRolesToNames(ensembleTimer));
        return "ensemble-timer";
    }

    @PostMapping("/timer-view/{ensembleId}")
    public String createTimerView(@PathVariable("ensembleId") Long id) {
        ensembleTimerHolder.createTimerFor(EnsembleId.of(id));
        return "redirect:/admin/timer-view/" + id;
    }

    @PostMapping("/start-timer/{ensembleId}")
    public ResponseEntity<Void> startTimer(@PathVariable("ensembleId") Long id) {
        ensembleTimerHolder.startTimerFor(EnsembleId.of(id), Instant.now());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/rotate-timer/{ensembleId}")
    public ResponseEntity<Void> rotateTimer(@PathVariable("ensembleId") Long id) {
        ensembleTimerHolder.rotateTimerFor(EnsembleId.of(id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/pause-timer/{ensembleId}")
    public ResponseEntity<Void> pauseTimer(@PathVariable("ensembleId") Long id) {
        ensembleTimerHolder.pauseTimerFor(EnsembleId.of(id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resume-timer/{ensembleId}")
    public ResponseEntity<Void> resumeTimer(@PathVariable("ensembleId") Long id) {
        ensembleTimerHolder.resumeTimerFor(EnsembleId.of(id));
        return ResponseEntity.noContent().build();
    }

}
