package com.jitterted.mobreg.domain;

import java.util.List;

public record Rotation(List<MemberId> participants) {

    // try and use rotate?  Collections.rotate(participants, 1)

    public MemberId driver() {
        return participants.getFirst();
    }

    public MemberId navigator() {
        return participants.get(1);
    }

    public MemberId nextDriver() {
        return participants.get(2);
    }

    public List<MemberId> restOfParticipants() {
        return participants.subList(3, 5);
    }
}
