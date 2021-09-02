package com.jitterted.mobreg.domain;

public class HuddleAlreadyCompletedException extends RuntimeException {
    public HuddleAlreadyCompletedException() {
        super();
    }

    public HuddleAlreadyCompletedException(String message) {
        super(message);
    }
}
