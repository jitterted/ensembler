package com.jitterted.mobreg.application.port;

import com.jitterted.mobreg.domain.CountdownTimer;
import com.jitterted.mobreg.domain.EnsembleTimer;

public interface Broadcaster {
    void sendCurrentTimer(EnsembleTimer ensembleTimer);

    void sendEvent(CountdownTimer.TimerState timerState);
}
