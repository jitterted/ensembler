package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.domain.EnsembleId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EnsembleTimerController {
    private final EnsembleTimerHolder ensembleTimerHolder;

    @Autowired
    public EnsembleTimerController(EnsembleTimerHolder ensembleTimerHolder) {
        this.ensembleTimerHolder = ensembleTimerHolder;
    }

    @PostMapping("/admin/timer-view")
    public String gotoTimerView(@RequestParam("ensembleId") Long id) {
        ensembleTimerHolder.timerFor(new EnsembleId(id));
        return "redirect:/admin/timer-view/" + id;
    }
}
