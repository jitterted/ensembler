package com.jitterted.mobreg.adapter;

import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.Rotation;
import com.jitterted.mobreg.domain.TimeRemaining;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TimerToHtmlTransformer {

    public static String htmlFor(EnsembleTimer ensembleTimer) {
        return switch (ensembleTimer.state()) {
            case WAITING_TO_START -> htmlForWaitingToStart(ensembleTimer);
            case RUNNING -> htmlForRunning(ensembleTimer);
            case PAUSED -> htmlForPaused(ensembleTimer);
            case FINISHED -> htmlForFinished(ensembleTimer);
        };
    }

    private static String htmlForPaused(EnsembleTimer ensembleTimer) {
        return htmlForTimerControlButtonContainer(ensembleTimer, new Button("/member/resume-timer", "Resume Timer", "blue"))
               + htmlForTimerContainer(ensembleTimer.timeRemaining(), "paused");
    }

    private static String htmlForWaitingToStart(EnsembleTimer ensembleTimer) {
        return htmlForTimerControlButtonContainer(ensembleTimer, new Button("/member/start-timer", "Start Timer", "blue"))
               + htmlForTimerContainer(ensembleTimer.timeRemaining(), "running")
               + htmlForSwappingInRotationMembers(ensembleTimer.rotation());
    }

    private static String htmlForTimerControlButtonContainer(
            EnsembleTimer ensembleTimer,
            Button... buttons) {
        // language=html
        return """
               <swap id="timer-control-container"
                     hx-swap-oob="innerHTML"
                     hx-swap="none">
               """ +
               Arrays.stream(buttons)
                     .map(button -> htmlForTimerControlButton(ensembleTimer, button))
                     .collect(Collectors.joining())
               +
               """
               </swap>
               """;
    }

    private static String htmlForTimerControlButton(EnsembleTimer ensembleTimer, Button button) {
        return """
                   <button hx-post="%s/%s"
                           class="bg-%3$s-500 hover:bg-%s-700 text-white font-bold py-2 px-4 rounded">
                       %s
                   </button>
               """.formatted(button.buttonEndpointUrl(),
                             ensembleTimer.ensembleId().id(),
                             button.backgroundColor(),
                             button.buttonLabel());
    }

    public static String htmlForSwappingInRotationMembers(Rotation rotation) {
        return """
               <swap-container id="%s" hx-swap-oob="innerHTML">
                   %s
               </swap-container>
               <swap-container id="%s" hx-swap-oob="innerHTML">
                   %s
               </swap-container>
               <swap-container id="%s" hx-swap-oob="innerHTML">
                   %s
               </swap-container>
               """.formatted(RotationRole.ROLE_DRIVER.idString(),
                             rotation.driver().firstName(),
                             RotationRole.ROLE_NAVIGATOR.idString(),
                             rotation.navigator().firstName(),
                             RotationRole.ROLE_NEXT_DRIVER.idString(),
                             rotation.nextDriver().firstName())
               + """
                 <swap-container id="%s" hx-swap-oob="innerHTML">
                 %s
                 </swap-container>
                 """.formatted(RotationRole.ROLE_REST_OF_PARTICIPANTS.idString(),
                               htmlForRestOfParticipants(rotation.restOfParticipants()));
    }

    public static String htmlForRestOfParticipants(List<Member> restOfParticipants) {
        return restOfParticipants.stream()
                                 .map(Member::firstName)
                                 .map("    <p>%s</p>"::formatted)
                                 .reduce((left, right) -> left + "\n" + right)
                                 .orElse("    <p class=\"text-xl\">(no other participants)</p>");
    }

    // language=html
    private static String htmlForRunning(EnsembleTimer ensembleTimer) {
        return htmlForTimerControlButtonContainer(
                ensembleTimer,
                new Button("/member/pause-timer", "Pause Timer", "blue"),
                new Button("/member/reset-timer", "Reset Timer", "red"))
               +
               htmlForTimerContainer(ensembleTimer.timeRemaining(), "running");
    }

    // language=html
    private static String htmlForTimerContainer(TimeRemaining timeRemaining, String cssState) {
        double percentRemaining = timeRemaining.percent();
        return """
               <div id="timer-container"
                    class="circle circle-%s"
                    style="background: conic-gradient(%s 0%% %f%%, black %f%% 100%%);">
                   <svg class="progress-ring">
                       <circle class="progress-circle"/>
                   </svg>
                   <div class="timer-text-container timer-%s">
                       <div class="timer-text">%d:%02d</div>
                   </div>
               </div>
               """.formatted(cssState,
                             cssState.equals("running") ? "lightgreen" : "#FFD033",
                             percentRemaining, percentRemaining,
                             cssState,
                             timeRemaining.minutes(),
                             timeRemaining.seconds());
    }

    // language=html
    private static String htmlForFinished(EnsembleTimer ensembleTimer) {
        return htmlForTimerControlButtonContainer(ensembleTimer,
                                                  new Button("/member/rotate-timer", "Next Rotation", "blue"))
               +
               """ 
               <div id="timer-container"
                    class="circle circle-finished">
                   <div class="timer-text-container timer-finished">
                       <div class="timer-text">next</div>
                   </div>
               </div>
               """;
    }

    private record Button(String buttonEndpointUrl, String buttonLabel, String backgroundColor) {
    }
}
