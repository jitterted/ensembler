package com.jitterted.mobreg.adapter.out.email;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Manual, non-automated test: sends a real email through the live Brevo API.
 * Disabled so it never runs as part of the normal build.
 * <p>
 * To run: set the BREVO_API_KEY environment variable, change RECIPIENT_EMAIL
 * below to an address you can check, then run this test directly from your IDE.
 */
@Disabled("manual test that sends a real email via Brevo, run directly from the IDE")
public class BrevoEmailerManualTest {

    private static final String RECIPIENT_EMAIL = "tedyoung@gmail.com";

    @Test
    void sendsRealEmailViaBrevo() {
        String apiKey = System.getenv("BREVO_API_KEY");
        assertThat(apiKey)
                .as("BREVO_API_KEY environment variable must be set to run this manual test")
                .isNotBlank();

        BrevoEmailer brevoEmailer = new BrevoEmailer(apiKey, new SimpleMeterRegistry());

        EmailToSend emailToSend = new EmailToSend(
                "Ensembler Manual Test Email",
                "<!DOCTYPE html><html><body><h1>Manual Test</h1><p>This is a manual test email sent via BrevoEmailer.</p></body></html>",
                RECIPIENT_EMAIL);

        brevoEmailer.send(emailToSend);

        System.out.println("Check the logs above for a successful response, and check the inbox of " + RECIPIENT_EMAIL);
    }
}
