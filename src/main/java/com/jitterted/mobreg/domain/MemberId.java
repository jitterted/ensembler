package com.jitterted.mobreg.domain;

public record MemberId(long id) {
    public static MemberId of(long id) {
        return new MemberId(id);
    }
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "=" + id;
    }
}
