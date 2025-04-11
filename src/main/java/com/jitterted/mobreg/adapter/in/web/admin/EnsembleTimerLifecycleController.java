package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Duration;

@Controller
@RequestMapping("/admin")
public class EnsembleTimerLifecycleController {
    private final EnsembleTimerHolder ensembleTimerHolder;
    private final RandomShuffler shuffler;

    @Autowired
    public EnsembleTimerLifecycleController(EnsembleTimerHolder ensembleTimerHolder) {
        this.ensembleTimerHolder = ensembleTimerHolder;
        shuffler = new RandomShuffler();
    }

    @PostMapping("/create-timer/{ensembleId}")
    public String createTimer(@PathVariable("ensembleId") Long id, 
                             @RequestParam(value = "duration", required = false) Integer durationMinutes) {
        Duration duration = (durationMinutes != null && durationMinutes == 5) 
                          ? Duration.ofMinutes(5) 
                          : EnsembleTimer.DEFAULT_TIMER_DURATION;
        ensembleTimerHolder.createTimerFor(EnsembleId.of(id), shuffler, duration);
        return "redirect:/member/timer-view/" + id;
    }

    @PostMapping("/delete-timer/{ensembleId}")
    @ResponseBody
    public String deleteTimer(@PathVariable("ensembleId") Long id) {
        ensembleTimerHolder.deleteTimer(EnsembleId.of(id));
        return timerState(id);
    }

    @GetMapping("/ensemble-timer-state/{ensembleId}")
    @ResponseBody
    public String timerState(@PathVariable("ensembleId") Long id) {
        String statusText = htmlForTimerStatusText(id);
        String buttonHtml = htmlForTimerButton(id);
        // language=html
        return """
               <swap id="timer-status-container" hx-swap-oob="innerHTML">
               %s</swap>
               <swap id="timer-button-container" hx-swap-oob="innerHTML">
               %s</swap>
               """.formatted(statusText.indent(4),
                             buttonHtml.indent(4));
    }

    public String htmlForTimerButton(Long id) {
        if (ensembleTimerHolder.hasTimerFor(EnsembleId.of(id))) {
            return
                    """
                    <button class="inline-flex justify-center rounded-md border border-transparent bg-red-500 px-4 py-2 text-sm font-semibold text-white shadow-sm hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-300 focus:ring-offset-2"
                            hx-post="/admin/delete-timer/%s"
                    >
                        Delete Timer
                    </button>
                    """.formatted(id);
        }
        return
                """
                <div class="flex flex-col space-y-4">
                    <form action="/admin/create-timer/%s" method="post">
                        <button class="inline-flex items-center rounded-md bg-indigo-600 px-4 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-500"
                        >
                            Create 4-Minute Timer
                        </button>
                    </form>
                    <form action="/admin/create-timer/%s" method="post">
                        <input type="hidden" name="duration" value="5">
                        <button class="inline-flex items-center rounded-md bg-indigo-600 px-4 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-500"
                        >
                            Create 5-Minute Timer
                        </button>
                    </form>
                </div>""".formatted(id, id);
    }

    public String htmlForTimerStatusText(Long id) {
        if (ensembleTimerHolder.hasTimerFor(EnsembleId.of(id))) {
            return """
                   <p>A timer is currently running for this Ensemble
                       <a class="underline font-semibold text-blue-600"
                          href="/member/timer-view/%s">here</a>.
                   </p>""".formatted(id);
        }

        return ensembleTimerHolder
                .currentTimer()
                .map(this::htmlWithLinkToTimer)
                .orElse("<p>No timer currently exists for this Ensemble.</p>");
    }

    private String htmlWithLinkToTimer(EnsembleTimer timer) {
        return """
               <p>Timer exists for
                   <a class="underline font-semibold text-blue-600"
                   href="/admin/ensemble/%s">another ensemble</a>.
                   Create will replace that timer with a new one for this Ensemble.
               </p>""".formatted(timer.ensembleId().id());
    }

}
