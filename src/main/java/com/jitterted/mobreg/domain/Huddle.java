package com.jitterted.mobreg.domain;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

// This is the Aggregate Root for Huddles
public class Huddle {
    private static final int MAX_REGISTERED_MEMBERS = 5;
    private HuddleId id;

    private final String name;
    private final ZonedDateTime startDateTime;
    private URI zoomMeetingLink;
    private final Set<MemberId> memberIds = new HashSet<>();
    private boolean isCompleted = false;
    private URI recordingLink = URI.create("");

    public Huddle(String name, ZonedDateTime startDateTime) {
        this(name, URI.create("https://zoom.us"), startDateTime);
    }

    public Huddle(String name, URI zoomMeetingLink, ZonedDateTime startDateTime) {
        this.name = name;
        this.zoomMeetingLink = zoomMeetingLink;
        this.startDateTime = startDateTime;
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

    public void register(MemberId memberId) {
        requireNotCompleted();
        requireHasSpace();
        memberIds.add(memberId);
    }

    private void requireHasSpace() {
        if (isFull()) {
            throw new HuddleIsAlreadyFullException("Currently have " + registeredMemberCount() + " registered.");
        }
    }

    public boolean canRegister() {
        return !isFull();
    }

    private boolean isFull() {
        return registeredMemberCount() == MAX_REGISTERED_MEMBERS;
    }

    public boolean isRegistered(MemberId memberId) {
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

    public void linkToRecordingAt(URI recordingLink) {
        this.recordingLink = recordingLink;
    }

    public URI recordingLink() {
        return recordingLink;
    }

    private void requireNotCompleted() {
        if (isCompleted) {
            throw new HuddleAlreadyCompletedException();
        }
    }

    public HuddleId getId() {
        return id;
    }

    public void setId(HuddleId id) {
        this.id = id;
    }
}
