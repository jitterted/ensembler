package com.jitterted.mobreg.domain.port;

import java.net.URI;

public interface Notifier {
    int newHuddleOpened(String description, URI registrationLink);
}
