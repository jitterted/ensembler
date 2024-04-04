package com.jitterted.mobreg.adapter.out.websocket;

import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.EnsembleTimerFactory;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberFactory;
import com.jitterted.mobreg.domain.Rotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static com.jitterted.mobreg.adapter.out.websocket.TimerToHtmlTransformer.htmlForSwappingInRotationMembers;
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
                           <swap-container id="driver" hx-swap-oob="innerHTML">
                               <p>Two</p>
                           </swap-container>
                           <swap-container id="navigator" hx-swap-oob="innerHTML">
                               <p>Three</p>
                           </swap-container>
                           <swap-container id="nextDriver" hx-swap-oob="innerHTML">
                               <p>One</p>
                           </swap-container>
                           <swap-container id="restOfParticipants" hx-swap-oob="innerHTML">
                               <p>Four</p>
                               <p>Five</p>
                           </swap-container>
                           """);
    }

    @Test
    void timerRunningAfterSomeTimePassedHtmlIsCorrect() {
        EnsembleTimer ensembleTimer = createTimerWithTickAt30SecondsAfterStart(763);

        String timerHtml = TimerToHtmlTransformer.htmlFor(ensembleTimer);

        assertThat(timerHtml)
                .isEqualTo("""    
                           <button id="timer-control-button"
                                   hx-swap-oob="outerHTML"
                                   hx-swap="none"
                                   hx-post="/admin/pause-timer/763"
                                   class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                               Pause Timer
                           </button>
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
    void timerPausedHtmlIsCorrect() {
        EnsembleTimer ensembleTimer = createTimerWithTickAt30SecondsAfterStart(827);
        ensembleTimer.pause();

        String timerHtml = TimerToHtmlTransformer.htmlFor(ensembleTimer);

        assertThat(timerHtml)
                .isEqualTo("""    
                           <button id="timer-control-button"
                                   hx-swap-oob="outerHTML"
                                   hx-swap="none"
                                   hx-post="/admin/resume-timer/827"
                                   class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                               Resume Timer
                           </button>
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

    private EnsembleTimer createTimerWithTickAt30SecondsAfterStart(int id) {
        EnsembleTimer ensembleTimer = EnsembleTimerFactory
                .createTimerWith4MinuteDurationAndIdOf(EnsembleId.of(id));
        Instant timerStartedAt = Instant.now();
        ensembleTimer.startTimerAt(timerStartedAt);
        ensembleTimer.tick(timerStartedAt.plusSeconds(30));
        return ensembleTimer;
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

    @Nested
    class RotationHtml {
        private static final Member MEMBER1 = MemberFactory.createMember(1, "One", "IRRELEVANT_GITHUB_USERNAME");
        private static final Member MEMBER2 = MemberFactory.createMember(2, "Two", "IRRELEVANT_GITHUB_USERNAME");
        private static final Member MEMBER3 = MemberFactory.createMember(3, "Three", "IRRELEVANT_GITHUB_USERNAME");
        private static final Member MEMBER4 = MemberFactory.createMember(4, "Four", "IRRELEVANT_GITHUB_USERNAME");
        private static final Member MEMBER5 = MemberFactory.createMember(5, "Five", "IRRELEVANT_GITHUB_USERNAME");

        @Test
        void isCorrectForThreeMembers() {
            Rotation rotation = new Rotation(List.of(MEMBER1, MEMBER2, MEMBER3));

            String actual = htmlForSwappingInRotationMembers(rotation);

            assertThat(actual)
                    .isEqualTo(""" 
                           <swap-container id="driver" hx-swap-oob="innerHTML">
                               <p>Two</p>
                           </swap-container>
                           <swap-container id="navigator" hx-swap-oob="innerHTML">
                               <p>Three</p>
                           </swap-container>
                           <swap-container id="nextDriver" hx-swap-oob="innerHTML">
                               <p>One</p>
                           </swap-container>
                           <swap-container id="restOfParticipants" hx-swap-oob="innerHTML">
                               <p>(no other participants)</p>
                           </swap-container>
                           """);
        }

        @Test
        void isCorrectForFiveMembers() {
            Rotation rotation = new Rotation(List.of(
                    MEMBER1, MEMBER2, MEMBER3, MEMBER4, MEMBER5));

            String actual = htmlForSwappingInRotationMembers(rotation);

            assertThat(actual)
                    .isEqualTo(""" 
                           <swap-container id="driver" hx-swap-oob="innerHTML">
                               <p>Two</p>
                           </swap-container>
                           <swap-container id="navigator" hx-swap-oob="innerHTML">
                               <p>Three</p>
                           </swap-container>
                           <swap-container id="nextDriver" hx-swap-oob="innerHTML">
                               <p>One</p>
                           </swap-container>
                           <swap-container id="restOfParticipants" hx-swap-oob="innerHTML">
                               <p>Four</p>
                               <p>Five</p>
                           </swap-container>
                           """);
        }
    }

}