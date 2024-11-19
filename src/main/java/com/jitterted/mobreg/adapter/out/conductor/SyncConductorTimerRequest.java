package com.jitterted.mobreg.adapter.out.conductor;

import com.jitterted.mobreg.domain.CountdownTimer;
import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.Member;
import org.jetbrains.annotations.NotNull;

import java.util.List;

record SyncConductorTimerRequest(
        int timeRemainingSeconds,
        String state,
        String driver,
        String navigator,
        String nextDriver,
        List<String> restOfParticipants) {

    public static SyncConductorTimerRequest from(EnsembleTimer timer) {
        return new SyncConductorTimerRequest(
                timer.timeRemaining().minutes() * 60 +
                timer.timeRemaining().seconds(),
                mapTimerState(timer.state()),
                timer.rotation().driver().firstName(),
                timer.rotation().navigator().firstName(),
                timer.rotation().nextDriver().firstName(),
                timer.rotation().restOfParticipants().stream()
                        .map(Member::firstName)
                        .toList()
        );
    }

    @NotNull
    private static String mapTimerState(CountdownTimer.TimerState state) {
        return switch (state) {
            case WAITING_TO_START -> "Waiting";
            case RUNNING -> "Running";
            case PAUSED -> "Paused";
            case FINISHED -> "Finished";
        };
    }

}
