package com.jitterted.mobreg.domain;

public record MemberId(long id) {
    public static MemberId of(long id) {
        return new MemberId(id);
    }
}
