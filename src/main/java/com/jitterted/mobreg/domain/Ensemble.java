package com.jitterted.mobreg.domain;

import com.google.common.collect.ImmutableSet;

import java.net.URI;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

// This is the Aggregate Root for Ensembles
public class Ensemble {
    private static final int MAX_ACCEPTED_MEMBERS = 5;
    private static final TemporalAmount IN_PROGRESS_GRACE_PERIOD_MINUTES = Duration.ofMinutes(10);

    private EnsembleId id;

    private String name;
    private ZonedDateTime startDateTime; // PRIMITIVE OBSESSION
    private Duration duration = Duration.ofHours(1).plusMinutes(55);
    private ConferenceDetails conferenceDetails;
    private final Set<MemberId> membersWhoAccepted = new HashSet<>();
    private final Set<MemberId> membersWhoDeclined = new HashSet<>();
    private Status status = Status.SCHEDULED;
    private URI recordingLink = URI.create("");

    public Ensemble(String name, ZonedDateTime startDateTime) {
        this(name, URI.create("https://zoom.us"), startDateTime);
    }

    public Ensemble(String name, URI meetingLink, ZonedDateTime startDateTime) {
        this.name = name;
        this.conferenceDetails = new ConferenceDetails("", URI.create(""), meetingLink);
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

    public Stream<MemberId> acceptedMembers() {
        return ImmutableSet.copyOf(membersWhoAccepted).stream();
    }

    public void acceptedBy(MemberId memberId) {
        requireNotCompleted();
        requireNotCanceled();
        requireHasSpace();
        membersWhoAccepted.add(memberId);
        membersWhoDeclined.remove(memberId);
    }

    private void requireNotCanceled() {
        if (isCanceled()) {
            throw new EnsembleCanceled("Ensemble (%s) is Canceled".formatted(id));
        }
    }

    public boolean isDeclined(MemberId memberId) {
        return membersWhoDeclined.contains(memberId);
    }

    public void declinedBy(MemberId memberId) {
        membersWhoAccepted.remove(memberId);
        membersWhoDeclined.add(memberId);
    }

    public Stream<MemberId> declinedMembers() {
        return ImmutableSet.copyOf(membersWhoDeclined).stream();
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
        return acceptedCount() == MAX_ACCEPTED_MEMBERS;
    }

    public boolean isAccepted(MemberId memberId) {
        return membersWhoAccepted.contains(memberId);
    }

    public URI zoomMeetingLink() {
        return conferenceDetails.joinUrl();
    }

    public void changeMeetingLinkTo(URI meetingLink) {
        this.conferenceDetails = new ConferenceDetails("", URI.create(""), meetingLink);
    }

    public void complete() {
        requireNotCanceled();
        status = Status.COMPLETED;
    }

    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }

    public void linkToRecordingAt(URI recordingLink) {
        this.recordingLink = recordingLink;
    }

    public URI recordingLink() {
        return recordingLink;
    }

    private void requireNotCompleted() {
        if (isCompleted()) {
            throw new EnsembleCompleted("Ensemble (%s) is Completed".formatted(id));
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
        MemberStatus status = WhenSpaceRsvp.memberStatus(this, memberId, now);
        if (isInGracePeriod(now) && status != MemberStatus.HIDDEN) {
            status = MemberStatus.IN_GRACE_PERIOD;
        }
        if (isBetweenExclusive(now, startDateTime.plus(IN_PROGRESS_GRACE_PERIOD_MINUTES), startDateTime.plus(duration))) {
            status = MemberStatus.HIDDEN;
        }
        return status;
    }

    private boolean isInGracePeriod(ZonedDateTime now) {
        return isBetweenExclusive(now, startDateTime, startDateTime.plus(IN_PROGRESS_GRACE_PERIOD_MINUTES));
    }

    private boolean isBetweenExclusive(ZonedDateTime now, ZonedDateTime start, ZonedDateTime end) {
        return now.isAfter(start) && now.isBefore(end);
    }

    private boolean isAfterStartTime(ZonedDateTime now) {
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

    public int declinedCount() {
        return membersWhoDeclined.size();
    }

    public boolean isCanceled() {
        return status == Status.CANCELED;
    }

    public void cancel() {
        requireNotCompleted();
        status = Status.CANCELED;
    }

    record WhenSpaceRsvp(When when, Space space, Rsvp rsvp) {
            // @formatter: off
        private static final Map<WhenSpaceRsvp, MemberStatus> STATE_TO_STATUS = Map.ofEntries(
                Map.entry(new WhenSpaceRsvp(When.STARTED,   Space.AVAILABLE,    Rsvp.UNKNOWN), MemberStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.AVAILABLE,    Rsvp.UNKNOWN), MemberStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.FUTURE,    Space.AVAILABLE,    Rsvp.UNKNOWN), MemberStatus.UNKNOWN),
                Map.entry(new WhenSpaceRsvp(When.STARTED,   Space.FULL,         Rsvp.UNKNOWN), MemberStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.FULL,         Rsvp.UNKNOWN), MemberStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.FUTURE,    Space.FULL,         Rsvp.UNKNOWN), MemberStatus.FULL),
                Map.entry(new WhenSpaceRsvp(When.STARTED,   Space.AVAILABLE,    Rsvp.DECLINED), MemberStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.AVAILABLE,    Rsvp.DECLINED), MemberStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.FUTURE,    Space.AVAILABLE,    Rsvp.DECLINED), MemberStatus.DECLINED),
                Map.entry(new WhenSpaceRsvp(When.STARTED,   Space.FULL,         Rsvp.DECLINED), MemberStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.FULL,         Rsvp.DECLINED), MemberStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.FUTURE,    Space.FULL,         Rsvp.DECLINED), MemberStatus.DECLINED_FULL),
                Map.entry(new WhenSpaceRsvp(When.STARTED,   Space.AVAILABLE,    Rsvp.ACCEPTED), MemberStatus.PENDING_COMPLETED),
                Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.AVAILABLE,    Rsvp.ACCEPTED), MemberStatus.COMPLETED),
                Map.entry(new WhenSpaceRsvp(When.FUTURE,    Space.AVAILABLE,    Rsvp.ACCEPTED), MemberStatus.ACCEPTED),
                Map.entry(new WhenSpaceRsvp(When.STARTED,   Space.FULL,         Rsvp.ACCEPTED), MemberStatus.PENDING_COMPLETED),
                Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.FULL,         Rsvp.ACCEPTED), MemberStatus.COMPLETED),
                Map.entry(new WhenSpaceRsvp(When.FUTURE,    Space.FULL,         Rsvp.ACCEPTED), MemberStatus.ACCEPTED))
                ;
            // @formatter:on
        private static When when(Ensemble ensemble, ZonedDateTime now) {
            if (ensemble.isCompleted()) {
                return When.COMPLETED;
            }
            return ensemble.isAfterStartTime(now) ? When.STARTED : When.FUTURE;
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
            STARTED,
            COMPLETED,
            FUTURE
        }

        enum Space {
            FULL,
            AVAILABLE
        }
    }

    private enum Status {
        SCHEDULED,
        COMPLETED,
        CANCELED
    }
}
