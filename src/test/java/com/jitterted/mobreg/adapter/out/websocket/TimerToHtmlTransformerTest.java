package com.jitterted.mobreg.adapter.out.websocket;

import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.EnsembleTimerFactory;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class TimerToHtmlTransformerTest {

    @Test
    void waitingToStartTimerHtmlIsCorrect() {
        EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimerWith4MinuteDuration();

        String timerHtml = TimerToHtmlTransformer.htmlFor(ensembleTimer);

        assertThat(timerHtml)
                .isEqualTo("""
                            <div id="timer-container"
                                 class="circle"
                                 style="background: conic-gradient(lightgreen 0% 100.000000%, black 100.000000% 100%);">
                                <svg class="progress-ring">
                                    <circle class="progress-circle"/>
                                </svg>
                                <div class="timer-circle">
                                   <div id="timer" class="font-bold">4:00</div>
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
                            <div id="timer-container"
                                 class="circle"
                                 style="background: conic-gradient(lightgreen 0% 87.500000%, black 87.500000% 100%);">
                                <svg class="progress-ring">
                                    <circle class="progress-circle"/>
                                </svg>
                                <div class="timer-circle">
                                   <div id="timer" class="font-bold">3:30</div>
                                </div>
                            </div>
                            """);

    }
}