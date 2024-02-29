package com.jitterted.mobreg.domain;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class EnsembleBuilder {

    private ConferenceDetails conferenceDetails;
    private ZonedDateTime startDateTime;
    private final List<MemberId> participantMemberIds = new ArrayList<>();
    private final List<MemberId> declinedMemberIds = new ArrayList<>();
    private String name;
    private boolean markAsCompleted;
    private Integer id;
    private boolean markAsCanceled;

    public EnsembleBuilder() {
    }

    public EnsembleBuilder accept(Member member) {
        participantMemberIds.add(member.getId());
        return this;
    }

    public EnsembleBuilder decline(Member member) {
        declinedMemberIds.add(member.getId());
        return this;
    }

    public Ensemble build() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        if (conferenceDetails != null) {
            ensemble.changeConferenceDetailsTo(conferenceDetails);
        }
        if (startDateTime != null) {
            ensemble.changeStartDateTimeTo(startDateTime);
        }
        if (name != null) {
            ensemble.changeNameTo(name);
        }
        if (markAsCompleted) {
            ensemble.complete();
        }
        if (markAsCanceled) {
            ensemble.cancel();
        }
        if (id != null) {
            ensemble.setId(EnsembleId.of(id));
        }

        participantMemberIds.forEach(ensemble::joinAsParticipant);
        declinedMemberIds.forEach(ensemble::declinedBy);

        return ensemble;
    }

    public EnsembleBuilder named(String name) {
        this.name = name;
        return this;
    }

    public EnsembleBuilder id(int id) {
        this.id = id;
        return this;
    }

    public EnsembleBuilder asCompleted() {
        markAsCompleted = true;
        return this;
    }

    public EnsembleBuilder asCanceled() {
        markAsCanceled = true;
        return this;
    }

    public EnsembleBuilder withConferenceDetails(String meetingId, String startUrl, String joinUrl) {
        conferenceDetails = new ConferenceDetails(meetingId, URI.create(startUrl), URI.create(joinUrl));
        return this;
    }

    public EnsembleBuilder endedInThePast() {
        return scheduled(ZonedDateTime.now().minusDays(1));
    }

    public EnsembleBuilder scheduled(ZonedDateTime startDateTime) {
        this.startDateTime = startDateTime;
        return this;
    }

    public EnsembleBuilder startsNow() {
        this.startDateTime = ZonedDateTime.now();
        return this;
    }
}
