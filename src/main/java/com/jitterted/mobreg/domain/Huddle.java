package com.jitterted.mobreg.domain;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

public class Huddle {
    private HuddleId id;

    private final String name;
    private final ZonedDateTime startDateTime;
    private URI zoomMeetingLink;
    private final Set<Member> members = new HashSet<>();

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
        return members.size();
    }

    public Set<Member> participants() {
        return Set.copyOf(members);
    }

    public void register(Member member) {
        members.add(member);
    }

    public HuddleId getId() {
        return id;
    }

    public void setId(HuddleId id) {
        this.id = id;
    }

    public boolean isRegisteredByUsername(String username) {
        return participants()
                .stream()
                .anyMatch(p -> p.githubUsername().equalsIgnoreCase(username));
    }
}
