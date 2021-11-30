package com.jitterted.mobreg.application;

public class EnsembleNotFoundException extends RuntimeException {
    public EnsembleNotFoundException() {
        super();
    }

    public EnsembleNotFoundException(String message) {
        super(message);
    }
}
