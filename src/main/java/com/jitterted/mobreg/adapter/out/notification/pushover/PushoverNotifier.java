package com.jitterted.mobreg.adapter.out.notification.pushover;

import com.jitterted.mobreg.domain.port.Notifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Component
public class PushoverNotifier implements Notifier {

    private final PushoverApiConfig pushoverApiConfig;

    @Autowired
    public PushoverNotifier(PushoverApiConfig pushoverApiConfig) {
        this.pushoverApiConfig = pushoverApiConfig;
    }

    @Override
    public int newHuddleOpened(String description, URI registrationLink) {
        String messageBody = """
                   New Ensemble '%s' has been scheduled.
                   Visit %s to register.
                """
                .formatted(description, registrationLink.toString());

        int status = send(messageBody);
        return status;
    }

    private int send(String messageBody) {
        PushoverMessageDto pushoverMessageDto = new PushoverMessageDto(pushoverApiConfig.getToken(),
                                                                       pushoverApiConfig.getGroupKeyMob1(),
                                                                       messageBody);

        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<Map<String, String>> mapStringStringTypeRef = new ParameterizedTypeReference<>(){};
        ResponseEntity<Map<String, String>> responseEntity = restTemplate.exchange("https://api.pushover.net/1/messages.json",
                                                                                   HttpMethod.POST,
                                                                                   new HttpEntity<>(pushoverMessageDto),
                                                                                   mapStringStringTypeRef);

        int status = Integer.parseInt(responseEntity.getBody().get("status"));
        return status;
    }

}
