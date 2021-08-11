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
    // GOAL: remove Set<Member> members, only use Set<MemberId>
    private final Set<Member> members = new HashSet<>();
    private final Set<MemberId> memberIds = new HashSet<>();

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

    // GOAL: replace with registeredMembers()
    @Deprecated
    public Set<Member> participants() {
        return Set.copyOf(members);
    }

    public Set<MemberId> registeredMembers() {
        return memberIds;
    }

    // GOAL: replace with registerById
    @Deprecated
    public void register(Member member) {
        members.add(member);
    }

    public HuddleId getId() {
        return id;
    }

    public void setId(HuddleId id) {
        this.id = id;
    }

    // GOAL: use isRegisteredById instead
    @Deprecated
    public boolean isRegisteredByUsername(String username) {
        return members.stream()
                      .anyMatch(p -> p.githubUsername().equalsIgnoreCase(username));
    }

    public void registerById(MemberId memberId) {
        memberIds.add(memberId);
    }

    public boolean isRegisteredById(MemberId memberId) {
        return memberIds.contains(memberId);
    }
}
