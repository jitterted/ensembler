package com.jitterted.mobreg.adapter.out.websocket;

import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.TimeRemaining;

public class TimerToHtmlTransformer {

    public static String htmlFor(EnsembleTimer ensembleTimer) {
        return switch (ensembleTimer.state()) {
            case FINISHED -> htmlForFinished();
            case WAITING_TO_START -> htmlForWaitingToStart(ensembleTimer);
            case RUNNING -> htmlForRunning(ensembleTimer);
        };
    }

    // language=html
    private static String htmlForWaitingToStart(EnsembleTimer ensembleTimer) {
        return """
               <swap-container id='timer-control-button' hx-swap-oob='innerHtml'>
                    Start Timer
               </swap-container>
               """
                + htmlForTimerContainer(ensembleTimer);
    }

    // language=html
    private static String htmlForRunning(EnsembleTimer ensembleTimer) {
        return """
               <swap-container id='timer-control-button' hx-swap-oob='innerHtml'>
                    Pause Timer
               </swap-container>
               """
                + htmlForTimerContainer(ensembleTimer);
    }

    // language=html
    private static String htmlForTimerContainer(EnsembleTimer ensembleTimer) {
        TimeRemaining timeRemaining = ensembleTimer.timeRemaining();
        double percentRemaining = timeRemaining.percent();
        return """
               <div id="timer-container"
                    class="circle circle-running"
                    style="background: conic-gradient(lightgreen 0%% %f%%, black %f%% 100%%);">
                   <svg class="progress-ring">
                       <circle class="progress-circle"/>
                   </svg>
                   <div class="timer-text-container timer-running">
                       <div class="timer-text">%d:%02d</div>
                   </div>
               </div>
               """.formatted(percentRemaining, percentRemaining,
                             timeRemaining.minutes(),
                             timeRemaining.seconds());
    }

    // language=html
    private static String htmlForFinished() {
        return """
               <div id="timer-container"
                    class="circle circle-finished">
                   <div class="timer-text-container timer-finished">
                       <div class="timer-text">next</div>
                   </div>
               </div>
               """;
    }

}
