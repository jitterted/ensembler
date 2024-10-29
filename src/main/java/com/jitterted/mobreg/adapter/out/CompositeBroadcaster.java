package com.jitterted.mobreg.adapter.out;

import com.jitterted.mobreg.application.port.Broadcaster;
import com.jitterted.mobreg.domain.EnsembleTimer;

import java.util.List;

public class CompositeBroadcaster implements Broadcaster {

    private final List<Broadcaster> broadcasters;

    public CompositeBroadcaster(List<Broadcaster> broadcasters) {
        this.broadcasters = broadcasters;
    }

    @Override
    public void sendCurrentTimer(EnsembleTimer ensembleTimer) {
        broadcasters.forEach(broadcaster -> broadcaster.sendCurrentTimer(ensembleTimer));
    }

    @Override
    public void sendEvent(EnsembleTimer.TimerEvent timerEvent) {
        broadcasters.forEach(broadcaster -> broadcaster.sendEvent(timerEvent));
    }
}
