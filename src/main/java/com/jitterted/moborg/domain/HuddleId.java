package com.jitterted.moborg.domain;

public record HuddleId(long id) {
    public static HuddleId of(long id) {
        return new HuddleId(id);
    }
}
