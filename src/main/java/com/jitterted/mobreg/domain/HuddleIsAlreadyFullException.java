package com.jitterted.mobreg.domain;

public class HuddleIsAlreadyFullException extends RuntimeException {
    public HuddleIsAlreadyFullException() {
        super();
    }

    public HuddleIsAlreadyFullException(String message) {
        super(message);
    }
}
