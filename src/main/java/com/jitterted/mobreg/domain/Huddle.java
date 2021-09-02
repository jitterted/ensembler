package com.jitterted.mobreg.domain;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

// This is the Aggregate Root for Huddles
public class Huddle {
    private HuddleId id;

    private final String name;
    private final ZonedDateTime startDateTime;
    private URI zoomMeetingLink;
    private final Set<MemberId> memberIds = new HashSet<>();
    private boolean isCompleted = false;

    public Huddle(String name, ZonedDateTime startDateTime) {
        this(name, URI.create("https://zoom.us"), startDateTime);
    }

    public Huddle(String name, URI zoomMeetingLink, ZonedDateTime startDateTime) {
        this.name = name;
        this.zoomMeetingLink = zoomMeetingLink;
        this.startDateTime = startDateTime;
    }

    public HuddleId getId() {
        return id;
    }

    public void setId(HuddleId id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public ZonedDateTime startDateTime() {
        return startDateTime;
    }

    public int registeredMemberCount() {
        return memberIds.size();
    }

    public Set<MemberId> registeredMembers() {
        return memberIds;
    }

    public void registerById(MemberId memberId) {
        requireNotCompleted();
        memberIds.add(memberId);
    }

    public boolean isRegisteredById(MemberId memberId) {
        return memberIds.contains(memberId);
    }

    public URI zoomMeetingLink() {
        return zoomMeetingLink;
    }

    public void complete() {
        isCompleted = true;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    private void requireNotCompleted() {
        if (isCompleted) {
            throw new HuddleAlreadyCompletedException();
        }
    }
}
