package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.in.web.admin.ParticipantsTransformer;
import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;

@Controller
@RequestMapping("/member")
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

    @PostMapping("/start-timer/{ensembleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void startTimer(@PathVariable("ensembleId") Long id) {
        ensembleTimerHolder.startTimerFor(EnsembleId.of(id), Instant.now());
    }

    @PostMapping("/reset-timer/{ensembleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetTimer(@PathVariable("ensembleId") Long id) {
    }

    @PostMapping("/rotate-timer/{ensembleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rotateTimer(@PathVariable("ensembleId") Long id) {
        ensembleTimerHolder.rotateTimerFor(EnsembleId.of(id));
    }

    @PostMapping("/pause-timer/{ensembleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pauseTimer(@PathVariable("ensembleId") Long id) {
        ensembleTimerHolder.pauseTimerFor(EnsembleId.of(id));
    }

    @PostMapping("/resume-timer/{ensembleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resumeTimer(@PathVariable("ensembleId") Long id) {
        ensembleTimerHolder.resumeTimerFor(EnsembleId.of(id));
    }

}
