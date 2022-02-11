package com.jitterted.mobreg.domain;

public class EnsembleCompleted extends RuntimeException {
    public EnsembleCompleted() {
        super();
    }

    public EnsembleCompleted(String message) {
        super(message);
    }
}
