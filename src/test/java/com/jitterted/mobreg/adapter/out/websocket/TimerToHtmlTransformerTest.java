package com.jitterted.mobreg.adapter.out.websocket;

import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.EnsembleTimerFactory;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class TimerToHtmlTransformerTest {

    @Test
    void waitingToStartTimerHtmlIsCorrect() {
        EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimerWith4MinuteDurationAndIdOf(EnsembleId.of(57));

        String timerHtml = TimerToHtmlTransformer.htmlFor(ensembleTimer);

        assertThat(timerHtml)
                .isEqualTo("""
                           <button id="timer-control-button"
                                   hx-swap-oob="outerHTML"
                                   hx-swap="none"
                                   hx-post="/admin/start-timer/57"
                                   class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                               Start Timer
                           </button>
                           <div id="timer-container"
                                class="circle circle-running"
                                style="background: conic-gradient(lightgreen 0% 100.000000%, black 100.000000% 100%);">
                               <svg class="progress-ring">
                                   <circle class="progress-circle"/>
                               </svg>
                               <div class="timer-text-container timer-running">
                                   <div class="timer-text">4:00</div>
                               </div>
                           </div>
                           """);
    }

    @Test
    void timerRunningAfterSomeTimePassedHtmlIsCorrect() {
        EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimerWith4MinuteDuration();
        Instant timerStartedAt = Instant.now();
        ensembleTimer.startTimerAt(timerStartedAt);
        ensembleTimer.tick(timerStartedAt.plusSeconds(30));

        String timerHtml = TimerToHtmlTransformer.htmlFor(ensembleTimer);

        assertThat(timerHtml)
                .isEqualTo("""    
                           <swap-container id='timer-control-button' hx-swap-oob='innerHTML'>
                                Pause Timer
                           </swap-container>
                           <div id="timer-container"
                                class="circle circle-running"
                                style="background: conic-gradient(lightgreen 0% 87.500000%, black 87.500000% 100%);">
                               <svg class="progress-ring">
                                   <circle class="progress-circle"/>
                               </svg>
                               <div class="timer-text-container timer-running">
                                   <div class="timer-text">3:30</div>
                               </div>
                           </div>
                           """);
    }

    @Test
    void timerFinishedHtmlIsCorrect() {
        EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimerWith4MinuteDurationAndIdOf(EnsembleId.of(78));
        EnsembleTimerFactory.pushTimerToFinishedState(ensembleTimer);

        String timerHtml = TimerToHtmlTransformer.htmlFor(ensembleTimer);

        assertThat(timerHtml)
                .isEqualTo("""
                           <button id="timer-control-button"
                                   hx-swap-oob="outerHTML"
                                   hx-swap="none"
                                   hx-post="/admin/rotate-timer/78"
                                   class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                               Next Rotation
                           </button>
                           <div id="timer-container"
                                class="circle circle-finished">
                               <div class="timer-text-container timer-finished">
                                   <div class="timer-text">next</div>
                               </div>
                           </div>
                           """);
    }
}