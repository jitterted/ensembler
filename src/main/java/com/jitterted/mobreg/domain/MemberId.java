package com.jitterted.mobreg.domain;

import java.util.Objects;

public final class MemberId {
    private final long id;

    MemberId(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MemberId) obj;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MemberId[" +
                "id=" + id + ']';
    }

    public static MemberId of(long id) {
        return new MemberId(id);
    }
}
