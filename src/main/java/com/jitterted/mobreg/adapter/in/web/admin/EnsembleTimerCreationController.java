package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.domain.EnsembleId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class EnsembleTimerCreationController {
    private final EnsembleTimerHolder ensembleTimerHolder;

    @Autowired
    public EnsembleTimerCreationController(EnsembleTimerHolder ensembleTimerHolder) {
        this.ensembleTimerHolder = ensembleTimerHolder;
    }

    @PostMapping("/create-timer/{ensembleId}")
    public String createTimerView(@PathVariable("ensembleId") Long id) {
        ensembleTimerHolder.createTimerFor(EnsembleId.of(id));
        return "redirect:/member/timer-view/" + id;
    }

}
