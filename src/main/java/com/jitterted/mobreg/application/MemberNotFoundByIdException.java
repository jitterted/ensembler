package com.jitterted.mobreg.application;

public class MemberNotFoundByIdException extends RuntimeException {
    public MemberNotFoundByIdException() {
        super();
    }

    public MemberNotFoundByIdException(String message) {
        super(message);
    }
}
