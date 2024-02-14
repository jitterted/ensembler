package com.jitterted.mobreg.adapter.out.websocket;

import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.TimeRemaining;

public class TimerToHtmlTransformer {

    public static String htmlFor(EnsembleTimer ensembleTimer) {
        TimeRemaining timeRemaining = ensembleTimer.timeRemaining();
        double percentRemaining = timeRemaining.percent();
        return """
                <div id="timer-container"
                     class="circle"
                     style="background: conic-gradient(lightgreen 0%% %f%%, black %f%% 100%%);">
                    <svg class="progress-ring">
                        <circle class="progress-circle"/>
                    </svg>
                    <div class="timer-circle">
                       <div id="timer" class="font-bold">%d:%02d</div>
                    </div>
                </div>
                """.formatted(percentRemaining, percentRemaining,
                              timeRemaining.minutes(),
                              timeRemaining.seconds());
    }

}
