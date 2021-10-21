package com.jitterted.mobreg.domain;

public class MemberNotFoundByIdException extends RuntimeException {
    public MemberNotFoundByIdException() {
        super();
    }

    public MemberNotFoundByIdException(String message) {
        super(message);
    }
}
