package com.jitterted.mobreg.adapter.out.websocket;

public enum RotationRole {
    ROLE_DRIVER("driver"),
    ROLE_NAVIGATOR("navigator"),
    ROLE_NEXT_DRIVER("nextDriver"),
    ROLE_REST_OF_PARTICIPANTS("restOfParticipants");

    private final String idString;

    RotationRole(String idString) {
        this.idString = idString;
    }

    public String idString() {
        return idString;
    }
}