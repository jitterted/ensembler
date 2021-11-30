package com.jitterted.mobreg.application.port;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.Member;

import java.net.URI;

public interface Notifier {
    int newHuddleOpened(String description, URI registrationLink);

    void memberRegistered(Huddle huddle, Member member);
}
