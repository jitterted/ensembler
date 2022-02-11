package com.jitterted.mobreg.domain;

public record EnsembleId(long id) {
    public static EnsembleId of(long id) {
        return new EnsembleId(id);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "=" + id;
    }
}
