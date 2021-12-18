package com.jitterted.mobreg.adapter.out.email;

import java.util.ArrayList;
import java.util.List;

public class SpyEmailer implements Emailer {

    private final List<EmailToSend> sentEmails = new ArrayList<>();

    @Override
    public void send(EmailToSend emailToSend) {
        sentEmails.add(emailToSend);
    }

    public List<EmailToSend> sentEmails() {
        return sentEmails;
    }
}
