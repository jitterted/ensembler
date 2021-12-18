package com.jitterted.mobreg.adapter.out.email;

public interface Emailer {
    void send(EmailToSend emailToSend);
}
