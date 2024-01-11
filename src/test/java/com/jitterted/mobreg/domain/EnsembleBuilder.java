package com.jitterted.mobreg.domain;

import java.net.URI;
import java.time.ZonedDateTime;

public class EnsembleBuilder {

    private Ensemble ensemble;
    private ConferenceDetails conferenceDetails;

    public EnsembleBuilder() {
        ensemble = EnsembleFactory.withStartTimeNow();
    }

    public EnsembleBuilder accept(Member member) {
        ensemble.joinAsParticipant(member.getId());
        return this;
    }

    public EnsembleBuilder decline(Member member) {
        ensemble.declinedBy(member.getId());
        return this;
    }

    public Ensemble build() {
        Ensemble ensembleToReturn = ensemble;
        if (conferenceDetails != null) {
            ensemble.changeConferenceDetailsTo(conferenceDetails);
        }
        // reset ensemble for next build (TODO: apply all properties upon build, not along the way)
        ensemble = null;
        return ensembleToReturn;
    }

    public EnsembleBuilder named(String name) {
        ensemble.changeNameTo(name);
        return this;
    }

    public EnsembleBuilder id(int id) {
        ensemble.setId(EnsembleId.of(id));
        return this;
    }

    public EnsembleBuilder asCompleted() {
        ensemble.complete();
        return this;
    }

    public EnsembleBuilder asCanceled() {
        ensemble.cancel();
        return this;
    }

    public EnsembleBuilder withConferenceDetails(String meetingId, String startUrl, String joinUrl) {
        conferenceDetails = new ConferenceDetails(meetingId, URI.create(startUrl), URI.create(joinUrl));
        return this;
    }

    public EnsembleBuilder endedInThePast() {
        ensemble = EnsembleFactory.withStartTime(ZonedDateTime.now().minusDays(1));
        return this;
    }
}
