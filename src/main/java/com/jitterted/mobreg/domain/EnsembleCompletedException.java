package com.jitterted.mobreg.domain;

public class EnsembleCompletedException extends RuntimeException {
    public EnsembleCompletedException() {
        super();
    }

    public EnsembleCompletedException(String message) {
        super(message);
    }
}
