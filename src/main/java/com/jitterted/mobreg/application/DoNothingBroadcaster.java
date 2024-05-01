package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.Broadcaster;
import com.jitterted.mobreg.domain.CountdownTimer;
import com.jitterted.mobreg.domain.EnsembleTimer;

public class DoNothingBroadcaster implements Broadcaster {
    @Override
    public void sendCurrentTimer(EnsembleTimer ensembleTimer) {
    }

    @Override
    public void sendEvent(CountdownTimer.TimerState timerState) {
    }
}
