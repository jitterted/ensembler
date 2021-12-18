package com.jitterted.mobreg.adapter.out.email;

public record EmailToSend(String subject, String body, String recipient) {
}
