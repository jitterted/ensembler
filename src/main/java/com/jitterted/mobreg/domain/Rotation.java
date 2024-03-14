package com.jitterted.mobreg.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
public class Rotation {
    private final List<Member> participants;

    public Rotation(List<Member> participants) {
        requireThreeOrMoreParticipants(participants);
        this.participants = new ArrayList<>(participants);
    }

    private void requireThreeOrMoreParticipants(List<Member> participants) {
        if (participants.size() < 3) {
            throw new NotEnoughParticipants("%d is too few participants, requires minimum of 3 participants."
                                                    .formatted(participants.size()));
        }
    }

    public Member nextDriver() {
        return participants.get(0);
    }

    public Member driver() {
        return participants.get(1);
    }

    public Member navigator() {
        return participants.get(2);
    }

    public List<Member> restOfParticipants() {
        return participants.stream().skip(3).toList();
    }

    /**
     * Rotation proceeds as follows:
     * <ul>
     * <li>next driver  -> driver</li>
     * <li>driver       -> navigator</li>
     * <li>navigator    -> participant1</li>
     * <li>participant1 -> participant2</li>
     * <li>participant2 -> next driver</li>
     * </ul>
     */
    public void rotate() {
        Collections.rotate(participants, 1);
    }

    @Override
    public String toString() {
        return "Next Driver: " + nextDriver() + "\n"
                + "Driver: " + driver() + "\n"
                + "Navigator: " + navigator() + "\n"
                + "Rest of participants: " + restOfParticipants();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Rotation) obj;
        return Objects.equals(this.participants, that.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participants);
    }

}
