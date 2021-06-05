package com.jitterted.mobreg.domain;

public record HuddleId(long id) {
    public static HuddleId of(long id) {
        return new HuddleId(id);
    }
}
