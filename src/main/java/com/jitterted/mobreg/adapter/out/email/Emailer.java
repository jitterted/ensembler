package com.jitterted.mobreg.adapter.out.email;

import java.util.Set;

public interface Emailer {
    void send(String subject, String body, Set<String> recipients);
}
