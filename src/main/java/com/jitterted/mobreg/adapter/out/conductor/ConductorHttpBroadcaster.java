package com.jitterted.mobreg.adapter.out.conductor;

import com.jitterted.mobreg.application.port.Broadcaster;
import com.jitterted.mobreg.domain.CountdownTimer;
import com.jitterted.mobreg.domain.EnsembleTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class ConductorHttpBroadcaster implements Broadcaster {
    private final RestClient restClient = RestClient.create();

    private static final Logger logger = LoggerFactory.getLogger(ConductorHttpBroadcaster.class);

    @Override
    public void sendCurrentTimer(EnsembleTimer ensembleTimer) {

        if (ensembleTimer.state() == CountdownTimer.TimerState.WAITING_TO_START) {
            CreateTimerRequest createTimerRequest = new CreateTimerRequest(
                    ensembleTimer.ensembleName(),
                    ensembleTimer.timeRemaining()
                                 .minutes() * 60);
            try {
                ResponseEntity<Void> responseEntity =
                        restClient.post()
                                  .uri("https://conductor-service-ld5mdxm4za-ey.a.run.app/timers")
                                  .contentType(APPLICATION_JSON)
                                  .body(createTimerRequest)
                                  .retrieve()
                                  .toBodilessEntity();
                System.out.println("POST response: " + responseEntity);
            } catch (RestClientException e) {
                logger.warn("Attempted to POST to Conductor service", e);
            }
        }
    }

    @Override
    public void sendEvent(EnsembleTimer.TimerEvent timerEvent) {
    }

    record CreateTimerRequest(String name, int durationSeconds) {
    }
}
