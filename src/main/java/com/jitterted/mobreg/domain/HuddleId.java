package com.jitterted.mobreg.domain;

import java.util.Objects;

public final class HuddleId {
    private final long id;

    HuddleId(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (HuddleId) obj;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "HuddleId[" +
                "id=" + id + ']';
    }

    public static HuddleId of(long id) {
        return new HuddleId(id);
    }
}
