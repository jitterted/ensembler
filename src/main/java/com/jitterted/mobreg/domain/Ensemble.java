package com.jitterted.mobreg.domain;

import com.google.common.collect.ImmutableSet;

import java.net.URI;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

// This is the Aggregate Root for Ensembles
public class Ensemble {
    private static final int MAX_PARTICIPANTS = 5;

    private EnsembleId id;

    private String name;
    private ZonedDateTime startDateTime; // PRIMITIVE OBSESSION
    private Duration duration = Duration.ofHours(1).plusMinutes(55);
    private ConferenceDetails conferenceDetails;
    private final Set<MemberId> membersAsParticipants = new HashSet<>();
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

    public int participantCount() {
        return membersAsParticipants.size();
    }

    public void joinAsParticipant(MemberId memberId) {
        requireNotCompleted();
        requireNotCanceled();
        requireHasSpace();
        membersAsParticipants.add(memberId);
        membersWhoDeclined.remove(memberId);
        membersAsSpectators.remove(memberId);
    }

    public Stream<MemberId> participants() {
        return ImmutableSet.copyOf(membersAsParticipants).stream();
    }

    private boolean isParticipant(MemberId memberId) {
        return membersAsParticipants.contains(memberId);
    }

    public void joinAsSpectator(MemberId memberId) {
        membersAsSpectators.add(memberId);
        membersAsParticipants.remove(memberId);
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
        membersAsParticipants.remove(memberId);
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
            throw new EnsembleFullException("Currently have " + participantCount() + " registered.");
        }
    }

    public boolean isFull() {
        return participantCount() == MAX_PARTICIPANTS;
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

    public boolean isInProgress(ZonedDateTime now) {
        return now.isAfter(startDateTime())
                && now.isBefore(startDateTime().plus(duration));
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

}
