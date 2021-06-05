package com.jitterted.mobreg.domain;

public class HuddleNotFoundException extends RuntimeException {
    public HuddleNotFoundException() {
        super();
    }

    public HuddleNotFoundException(String message) {
        super(message);
    }
}
