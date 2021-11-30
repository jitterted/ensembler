package com.jitterted.mobreg.application;

public class HuddleNotFoundException extends RuntimeException {
    public HuddleNotFoundException() {
        super();
    }

    public HuddleNotFoundException(String message) {
        super(message);
    }
}
