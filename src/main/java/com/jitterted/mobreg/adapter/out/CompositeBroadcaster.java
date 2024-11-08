package com.jitterted.mobreg.adapter.out;

import com.jitterted.mobreg.application.port.Broadcaster;
import com.jitterted.mobreg.domain.EnsembleTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CompositeBroadcaster implements Broadcaster {

    private static final Logger logger = LoggerFactory.getLogger(CompositeBroadcaster.class);
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
        broadcasters.forEach(broadcaster -> {
            try {
                broadcaster.sendEvent(timerEvent);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }
}
