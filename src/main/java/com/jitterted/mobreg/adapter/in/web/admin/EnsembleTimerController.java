package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.domain.EnsembleId;
import org.springframework.stereotype.Controller;

@Controller
public class EnsembleTimerController {
    private final EnsembleTimerHolder ensembleTimerHolder;

    public EnsembleTimerController(EnsembleTimerHolder ensembleTimerHolder) {
        this.ensembleTimerHolder = ensembleTimerHolder;
    }

    public String gotoTimerView(long id) {
        ensembleTimerHolder.timerFor(new EnsembleId(id));
        return "redirect:/admin/timer-view";
    }
}
