package com.jitterted.mobreg.adapter.out.email;

import java.util.Set;

public class SpyEmailer implements Emailer {

    private String subject;
    private String body;
    private Set<String> recipients = null;

    public Set<String> emailRecipients() {
        return recipients;
    }

    public String subject() {
        return subject;
    }

    public String body() {
        return body;
    }

    @Override
    public void send(String subject, String body, Set<String> recipients) {
        this.subject = subject;
        this.body = body;
        this.recipients = recipients;
    }
}
