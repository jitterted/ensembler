package com.jitterted.mobreg.domain;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// This is the Aggregate Root for Huddles
public class Huddle {
    private static final int MAX_REGISTERED_MEMBERS = 5;

    private HuddleId id;

    private String name;
    private ZonedDateTime startDateTime;
    private URI zoomMeetingLink;
    private final Set<MemberId> membersWhoAccepted = new HashSet<>(); // Accepted members
    private final Set<MemberId> membersWhoDeclined = new HashSet<>();
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
        return membersWhoAccepted.size();
    }

    public Set<MemberId> registeredMembers() {
        // TODO: provide Stream<> or ImmutableSet<>
        return membersWhoAccepted;
    }

    public void acceptedBy(MemberId memberId) {
        requireNotCompleted();
        requireHasSpace();
        membersWhoAccepted.add(memberId);
        membersWhoDeclined.remove(memberId);
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

    public boolean isAccepted(MemberId memberId) {
        return membersWhoAccepted.contains(memberId);
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

    public void changeNameTo(String newName) {
        requireNotNull(newName);
        name = newName;
    }

    private void requireNotNull(Object value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
    }

    public void changeStartDateTimeTo(ZonedDateTime newStartDateTime) {
        requireNotNull(newStartDateTime);
        startDateTime = newStartDateTime;
    }

    public MemberStatus statusFor(MemberId memberId, ZonedDateTime now) {
        When when = when(now);
        Space space = space();
        Rsvp rsvp = rsvpOf(memberId);
        return STATE_TO_STATUS.get(new WhenSpaceRsvp(when, space, rsvp));
    }

    private Space space() {
        return isFull() ? Space.FULL : Space.AVAILABLE;
    }

    private When when(ZonedDateTime now) {
        if (isCompleted()) {
            return When.COMPLETED;
        }
        return inThePast(now) ? When.PAST : When.FUTURE;
    }

    private boolean inThePast(ZonedDateTime now) {
        return now.isAfter(startDateTime);
    }

    public Rsvp rsvpOf(MemberId memberId) {
        if (isDeclined(memberId)) {
            return Rsvp.DECLINED;
        }
        if (isAccepted(memberId)) {
            return Rsvp.ACCEPTED;
        }
        return Rsvp.UNKNOWN;
    }

    private boolean isDeclined(MemberId memberId) {
        return membersWhoDeclined.contains(memberId);
    }

    public void declinedBy(MemberId memberId) {
        membersWhoAccepted.remove(memberId);
        membersWhoDeclined.add(memberId);
    }

    enum When {
        PAST,
        COMPLETED,
        FUTURE
    }

    enum Space {
        FULL,
        AVAILABLE
    }

    record WhenSpaceRsvp(When when, Space space, Rsvp rsvp) { }

    private static final Map<WhenSpaceRsvp, MemberStatus> STATE_TO_STATUS = Map.ofEntries(
            Map.entry(new WhenSpaceRsvp(When.PAST, Space.AVAILABLE, Rsvp.UNKNOWN), MemberStatus.HIDDEN),
            Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.AVAILABLE, Rsvp.UNKNOWN), MemberStatus.HIDDEN),
            Map.entry(new WhenSpaceRsvp(When.FUTURE, Space.AVAILABLE, Rsvp.UNKNOWN), MemberStatus.UNKNOWN),
            Map.entry(new WhenSpaceRsvp(When.PAST, Space.FULL, Rsvp.UNKNOWN), MemberStatus.HIDDEN),
            Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.FULL, Rsvp.UNKNOWN), MemberStatus.HIDDEN),
            Map.entry(new WhenSpaceRsvp(When.FUTURE, Space.FULL, Rsvp.UNKNOWN), MemberStatus.FULL),
            Map.entry(new WhenSpaceRsvp(When.PAST, Space.AVAILABLE, Rsvp.DECLINED), MemberStatus.HIDDEN),
            Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.AVAILABLE, Rsvp.DECLINED), MemberStatus.HIDDEN),
            Map.entry(new WhenSpaceRsvp(When.FUTURE, Space.AVAILABLE, Rsvp.DECLINED), MemberStatus.DECLINED),
            Map.entry(new WhenSpaceRsvp(When.PAST, Space.FULL, Rsvp.DECLINED), MemberStatus.HIDDEN),
            Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.FULL, Rsvp.DECLINED), MemberStatus.HIDDEN),
            Map.entry(new WhenSpaceRsvp(When.FUTURE, Space.FULL, Rsvp.DECLINED), MemberStatus.DECLINED_FULL),
            Map.entry(new WhenSpaceRsvp(When.PAST, Space.AVAILABLE, Rsvp.ACCEPTED), MemberStatus.PENDING_COMPLETED),
            Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.AVAILABLE, Rsvp.ACCEPTED), MemberStatus.COMPLETED),
            Map.entry(new WhenSpaceRsvp(When.FUTURE, Space.AVAILABLE, Rsvp.ACCEPTED), MemberStatus.ACCEPTED));
}
