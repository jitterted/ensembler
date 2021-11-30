package com.jitterted.mobreg.domain;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

// This is the Aggregate Root for Ensembles
public class Ensemble {
    private static final int MAX_REGISTERED_MEMBERS = 5;

    private EnsembleId id;

    private String name;
    private ZonedDateTime startDateTime;
    private URI zoomMeetingLink;
    private final Set<MemberId> membersWhoAccepted = new HashSet<>();
    private final Set<MemberId> membersWhoDeclined = new HashSet<>();
    private boolean isCompleted = false;
    private URI recordingLink = URI.create("");

    public Ensemble(String name, ZonedDateTime startDateTime) {
        this(name, URI.create("https://zoom.us"), startDateTime);
    }

    public Ensemble(String name, URI zoomMeetingLink, ZonedDateTime startDateTime) {
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

    public int acceptedCount() {
        return membersWhoAccepted.size();
    }

    public Set<MemberId> acceptedMembers() {
        // #28 TODO: provide Stream<> or ImmutableSet<>
        return membersWhoAccepted;
    }

    public void acceptedBy(MemberId memberId) {
        requireNotCompleted();
        requireHasSpace();
        membersWhoAccepted.add(memberId);
        membersWhoDeclined.remove(memberId);
    }

    public boolean isDeclined(MemberId memberId) {
        return membersWhoDeclined.contains(memberId);
    }

    public void declinedBy(MemberId memberId) {
        membersWhoAccepted.remove(memberId);
        membersWhoDeclined.add(memberId);
    }

    public Stream<MemberId> declinedMembers() {
        return membersWhoDeclined.stream();
    }

    private void requireHasSpace() {
        if (isFull()) {
            throw new EnsembleFullException("Currently have " + acceptedCount() + " registered.");
        }
    }

    public boolean canAccept() {
        return !isFull();
    }

    private boolean isFull() {
        return acceptedCount() == MAX_REGISTERED_MEMBERS;
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
            throw new EnsembleCompletedException();
        }
    }

    public EnsembleId getId() {
        return id;
    }

    public void setId(EnsembleId id) {
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
        return WhenSpaceRsvp.memberStatus(this, memberId, now);
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

    record WhenSpaceRsvp(When when, Space space, Rsvp rsvp) {
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
                Map.entry(new WhenSpaceRsvp(When.FUTURE, Space.AVAILABLE, Rsvp.ACCEPTED), MemberStatus.ACCEPTED),
                Map.entry(new WhenSpaceRsvp(When.PAST, Space.FULL, Rsvp.ACCEPTED), MemberStatus.PENDING_COMPLETED),
                Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.FULL, Rsvp.ACCEPTED), MemberStatus.COMPLETED),
                Map.entry(new WhenSpaceRsvp(When.FUTURE, Space.FULL, Rsvp.ACCEPTED), MemberStatus.ACCEPTED))
                ;

        private static When when(Ensemble ensemble, ZonedDateTime now) {
            if (ensemble.isCompleted()) {
                return When.COMPLETED;
            }
            return ensemble.inThePast(now) ? When.PAST : When.FUTURE;
        }

        private static Space space(Ensemble ensemble) {
            return ensemble.isFull() ? Space.FULL : Space.AVAILABLE;
        }

        private static MemberStatus memberStatus(Ensemble ensemble, MemberId memberId, ZonedDateTime now) {
            When when = when(ensemble, now);
            Space space = space(ensemble);
            Rsvp rsvp = ensemble.rsvpOf(memberId);
            WhenSpaceRsvp key = new WhenSpaceRsvp(when, space, rsvp);
            if (!STATE_TO_STATUS.containsKey(key)) {
                throw new IllegalStateException("No such state: " + key);
            }
            return STATE_TO_STATUS.get(key);
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
    }

}
