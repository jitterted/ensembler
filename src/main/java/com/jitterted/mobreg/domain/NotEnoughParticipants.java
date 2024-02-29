package com.jitterted.mobreg.domain;

public class NotEnoughParticipants extends RuntimeException {
    public NotEnoughParticipants() {
        super();
    }

    public NotEnoughParticipants(String message) {
        super(message);
    }
}
