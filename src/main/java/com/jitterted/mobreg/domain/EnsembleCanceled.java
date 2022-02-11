package com.jitterted.mobreg.domain;

public class EnsembleCanceled extends RuntimeException {
    public EnsembleCanceled(String message) {
        super(message);
    }

    public EnsembleCanceled(String message, Throwable cause) {
        super(message, cause);
    }
}
