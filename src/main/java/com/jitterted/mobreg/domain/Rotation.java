package com.jitterted.mobreg.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
public record Rotation(List<MemberId> participants) {

    public Rotation(List<MemberId> participants) {
        requireThreeOrMoreParticipants(participants);
        this.participants = new ArrayList<>(participants);
    }

    private void requireThreeOrMoreParticipants(List<MemberId> participants) {
        if (participants.size() < 3) {
            throw new NotEnoughParticipants("%d is too few participants, requires minimum of 3 participants."
                                                    .formatted(participants.size()));
        }
    }

    public MemberId nextDriver() {
        return participants.get(0);
    }

    public MemberId driver() {
        return participants.get(1);
    }

    public MemberId navigator() {
        return participants.get(2);
    }

    public List<MemberId> restOfParticipants() {
        return participants.subList(3, 5);
    }

    /**
     * Rotation proceeds as follows:
     *  <ul>
     *  <li>next driver  -> driver</li>
     *  <li>driver       -> navigator</li>
     *  <li>navigator    -> participant1</li>
     *  <li>participant1 -> participant2</li>
     *  <li>participant2 -> next driver</li>
     *  </ul>
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
}
