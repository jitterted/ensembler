package com.jitterted.moborg.domain;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Huddle {
    private HuddleId id;

    private final String name;
    private final ZonedDateTime startDateTime;
    private List<Participant> participants = new ArrayList<>();

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

    public List<Participant> participants() {
        return List.copyOf(participants);
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
