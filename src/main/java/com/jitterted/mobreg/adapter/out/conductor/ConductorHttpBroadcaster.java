package com.jitterted.mobreg.adapter.out.conductor;

import com.jitterted.mobreg.application.port.Broadcaster;
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
    private static final String BASE_URL = "https://conductor.suigi.dev";
    private final RestClient restClient = RestClient.create();

    private static final Logger logger = LoggerFactory.getLogger(ConductorHttpBroadcaster.class);

    @Override
    public void sendCurrentTimer(EnsembleTimer ensembleTimer) {
        try {
            String timerName = "Ensembler - " + ensembleTimer.ensembleName();
            String uri = BASE_URL + "/timers/" + timerName;
            ResponseEntity<Void> responseEntity =
                    restClient.put()
                              .uri(uri)
                              .contentType(APPLICATION_JSON)
                              .body(SyncConductorTimerRequest.from(ensembleTimer))
                              .retrieve()
                              .toBodilessEntity();
            System.out.println("PUT response: " + responseEntity);
        } catch (RestClientException e) {
            logger.warn("Attempted to PUT to Conductor service", e);
        }
    }

    @Override
    public void sendEvent(EnsembleTimer.TimerEvent timerEvent) {
    }

}
