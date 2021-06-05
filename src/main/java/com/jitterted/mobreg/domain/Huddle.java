package com.jitterted.mobreg.domain;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

public class Huddle {
    private HuddleId id;

    private final String name;
    private final ZonedDateTime startDateTime;
    private final Set<Participant> participants = new HashSet<>();

    public Huddle(String name, ZonedDateTime startDateTime) {
        this.name = name;
        this.startDateTime = startDateTime;
    }

    public String name() {
        return name;
    }

    public ZonedDateTime startDateTime() {
        return startDateTime;
    }

    public int numberRegistered() {
        return participants.size();
    }

    public Set<Participant> participants() {
        return Set.copyOf(participants);
    }

    public void register(Participant participant) {
        participants.add(participant);
    }

    public HuddleId getId() {
        return id;
    }

    public void setId(HuddleId id) {
        this.id = id;
    }
}
