package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.Member;

import java.net.URI;

public class DummyNotifier implements Notifier {
    @Override
    public int newHuddleOpened(String description, URI registrationLink) {
        return 0;
    }

    @Override
    public void memberRegistered(Huddle huddle, Member member) {

    }
}
