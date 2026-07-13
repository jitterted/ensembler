package com.jitterted.mobreg.adapter.out.email;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class BrevoEmailer implements Emailer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrevoEmailer.class);

    private static final String BREVO_SEND_EMAIL_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestClient restClient;
    private final String brevoApiKey;
    private final Counter emailSendFailureCounter;

    public BrevoEmailer(@Value("${brevo.api.key}") String brevoApiKey, MeterRegistry meterRegistry) {
        this.brevoApiKey = brevoApiKey;
        this.restClient = RestClient.create();
        this.emailSendFailureCounter = Counter.builder("email.send.failures")
                                               .description("Number of emails that failed to send via Brevo")
                                               .register(meterRegistry);
    }

    @Override
    public void send(EmailToSend emailToSend) {
        Map<String, Object> requestBody = Map.of(
                "sender", Map.of(
                        "email", "registration@ensembler.dev",
                        "name", "Ensembler Registration System"),
                "subject", emailToSend.subject(),
                "htmlContent", emailToSend.body(),
                "messageVersions", List.of(
                        Map.of("to", List.of(
                                Map.of("email", emailToSend.recipient())))));

        try {
            String response = restClient.post()
                                         .uri(BREVO_SEND_EMAIL_URL)
                                         .header("api-key", brevoApiKey)
                                         .header("Content-Type", "application/json")
                                         .header("accept", "application/json")
                                         .body(requestBody)
                                         .retrieve()
                                         .body(String.class);
            LOGGER.info("Email sent via Brevo, response={}, to email={}", response, emailToSend.recipient());
        } catch (Exception e) {
            emailSendFailureCounter.increment();
            LOGGER.warn("Exception when trying to send email via Brevo to {}", emailToSend.recipient(), e);
        }
    }
}
