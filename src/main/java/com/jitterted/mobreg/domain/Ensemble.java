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
    private final Set<MemberId> membersAsSpectators = new HashSet<>();
    private EnsembleState state = EnsembleState.SCHEDULED;
    private URI recordingLink = URI.create("");

    public Ensemble(String name, ZonedDateTime startDateTime) {
        this(name, URI.create("https://zoom.us"), startDateTime);
    }

    public Ensemble(String name, URI meetingLink, ZonedDateTime startDateTime) {
        this.name = name;
        this.conferenceDetails = new ConferenceDetails("", URI.create(""), meetingLink);
        this.startDateTime = startDateTime;
    }

    public EnsembleId getId() {
        return id;
    }

    public void setId(EnsembleId id) {
        this.id = id;
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

    public void joinAsParticipant(MemberId memberId) {
        requireNotCompleted();
        requireNotCanceled();
        requireHasSpace();
        membersWhoAccepted.add(memberId);
        membersWhoDeclined.remove(memberId);
        membersAsSpectators.remove(memberId);
    }

    public Stream<MemberId> acceptedMembers() {
        return ImmutableSet.copyOf(membersWhoAccepted).stream();
    }

    private boolean isParticipant(MemberId memberId) {
        return membersWhoAccepted.contains(memberId);
    }

    public void joinAsSpectator(MemberId memberId) {
        membersAsSpectators.add(memberId);
        membersWhoAccepted.remove(memberId);
        membersWhoDeclined.remove(memberId);
    }

    public Stream<MemberId> spectators() {
        return ImmutableSet.copyOf(membersAsSpectators).stream();
    }

    private boolean isSpectator(MemberId memberId) {
        return membersAsSpectators.contains(memberId);
    }

    public void declinedBy(MemberId memberId) {
        membersWhoDeclined.add(memberId);
        membersWhoAccepted.remove(memberId);
        membersAsSpectators.remove(memberId);
    }

    public Stream<MemberId> declinedMembers() {
        return ImmutableSet.copyOf(membersWhoDeclined).stream();
    }

    private boolean isDeclined(MemberId memberId) {
        return membersWhoDeclined.contains(memberId);
    }

    public MemberStatus memberStatusFor(MemberId memberId) {
        if (isParticipant(memberId)) {
            return MemberStatus.PARTICIPANT;
        } else if (isSpectator(memberId)) {
            return MemberStatus.SPECTATOR;
        } else if (isDeclined(memberId)) {
            return MemberStatus.DECLINED;
        } else {
            return MemberStatus.UNKNOWN;
        }
    }

    private void requireNotCanceled() {
        if (isCanceled()) {
            throw new EnsembleCanceled("Ensemble (%s) is Canceled".formatted(id));
        }
    }

    private void requireHasSpace() {
        if (isFull()) {
            throw new EnsembleFullException("Currently have " + acceptedCount() + " registered.");
        }
    }

    public boolean canAccept() {
        return !isFull();
    }

    public boolean isFull() {
        return acceptedCount() == MAX_ACCEPTED_MEMBERS;
    }

    public URI meetingLink() {
        return conferenceDetails.joinUrl();
    }

    public void changeMeetingLinkTo(URI meetingLink) {
        this.conferenceDetails = new ConferenceDetails("", URI.create(""), meetingLink);
    }

    public void complete() {
        requireNotCanceled();
        state = EnsembleState.COMPLETED;
    }

    public boolean isCompleted() {
        return state == EnsembleState.COMPLETED;
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

    @Deprecated // this information is now included in other functions
    public MemberEnsembleStatus statusFor(MemberId memberId, ZonedDateTime now) {
        MemberEnsembleStatus status = WhenSpaceRsvp.memberStatus(this, memberId, now);
        if ((isParticipant(memberId) || isSpectator(memberId))
                && isCanceled()) {
            return MemberEnsembleStatus.CANCELED;
        }
        if (isCanceled()) {
            return MemberEnsembleStatus.HIDDEN;
        }
        if (isInGracePeriod(now) && status != MemberEnsembleStatus.HIDDEN) {
            return MemberEnsembleStatus.IN_GRACE_PERIOD;
        }
        if (isBetweenExclusive(now, startDateTime.plus(IN_PROGRESS_GRACE_PERIOD_MINUTES), startDateTime.plus(duration))) {
            status = MemberEnsembleStatus.HIDDEN;
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
        if (isParticipant(memberId) || isSpectator(memberId)) {
            return Rsvp.ACCEPTED;
        }
        return Rsvp.UNKNOWN;
    }

    public int declinedCount() {
        return membersWhoDeclined.size();
    }

    public boolean isCanceled() {
        return state == EnsembleState.CANCELED;
    }

    public void cancel() {
        requireNotCompleted();
        state = EnsembleState.CANCELED;
    }

    public EnsembleState state() {
        return state;
    }

    public ConferenceDetails conferenceDetails() {
        return conferenceDetails;
    }

    public void changeConferenceDetailsTo(ConferenceDetails newConferenceDetails) {
        conferenceDetails = newConferenceDetails;
    }

    public void removeConferenceDetails() {
        conferenceDetails = ConferenceDetails.DELETED;
    }

    public boolean isPendingCompletedAsOf(ZonedDateTime now) {
        if (isCompleted() || isCanceled()) {
            return false;
        }
        return endTimeIsInThePast(now);
    }

    public boolean endTimeIsInThePast(ZonedDateTime now) {
        return now.isAfter(startDateTime.plus(duration));
    }

    public boolean isUpcoming(ZonedDateTime now) {
        if (isCanceled()) {
            return false;
        }
        return startDateTime.isAfter(now);
    }

    public boolean isRegistered(MemberId memberId) {
        return isParticipant(memberId) || isSpectator(memberId);
    }

    @Override
    public String toString() {
        return "Ensemble{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDateTime=" + startDateTime +
                ", state=" + state +
                '}';
    }

    public boolean isInProgress(ZonedDateTime now) {
        return now.isAfter(startDateTime()) && now.isBefore(startDateTime()
                                                                             .plus(duration));
    }

    record WhenSpaceRsvp(When when, Space space, Rsvp rsvp) {
        // @formatter: off
        private static final Map<WhenSpaceRsvp, MemberEnsembleStatus> STATE_TO_STATUS = Map.ofEntries(
                Map.entry(new WhenSpaceRsvp(When.STARTED, Space.AVAILABLE, Rsvp.UNKNOWN), MemberEnsembleStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.AVAILABLE, Rsvp.UNKNOWN), MemberEnsembleStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.FUTURE, Space.AVAILABLE, Rsvp.UNKNOWN), MemberEnsembleStatus.UNKNOWN),
                Map.entry(new WhenSpaceRsvp(When.STARTED, Space.FULL, Rsvp.UNKNOWN), MemberEnsembleStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.FULL, Rsvp.UNKNOWN), MemberEnsembleStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.FUTURE, Space.FULL, Rsvp.UNKNOWN), MemberEnsembleStatus.FULL),
                Map.entry(new WhenSpaceRsvp(When.STARTED, Space.AVAILABLE, Rsvp.DECLINED), MemberEnsembleStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.AVAILABLE, Rsvp.DECLINED), MemberEnsembleStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.FUTURE, Space.AVAILABLE, Rsvp.DECLINED), MemberEnsembleStatus.DECLINED),
                Map.entry(new WhenSpaceRsvp(When.STARTED, Space.FULL, Rsvp.DECLINED), MemberEnsembleStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.FULL, Rsvp.DECLINED), MemberEnsembleStatus.HIDDEN),
                Map.entry(new WhenSpaceRsvp(When.FUTURE, Space.FULL, Rsvp.DECLINED), MemberEnsembleStatus.DECLINED_FULL),
                Map.entry(new WhenSpaceRsvp(When.STARTED, Space.AVAILABLE, Rsvp.ACCEPTED), MemberEnsembleStatus.PENDING_COMPLETED),
                Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.AVAILABLE, Rsvp.ACCEPTED), MemberEnsembleStatus.COMPLETED),
                Map.entry(new WhenSpaceRsvp(When.FUTURE, Space.AVAILABLE, Rsvp.ACCEPTED), MemberEnsembleStatus.ACCEPTED),
                Map.entry(new WhenSpaceRsvp(When.STARTED, Space.FULL, Rsvp.ACCEPTED), MemberEnsembleStatus.PENDING_COMPLETED),
                Map.entry(new WhenSpaceRsvp(When.COMPLETED, Space.FULL, Rsvp.ACCEPTED), MemberEnsembleStatus.COMPLETED),
                Map.entry(new WhenSpaceRsvp(When.FUTURE, Space.FULL, Rsvp.ACCEPTED), MemberEnsembleStatus.ACCEPTED));

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

        private static MemberEnsembleStatus memberStatus(Ensemble ensemble, MemberId memberId, ZonedDateTime now) {
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

}
