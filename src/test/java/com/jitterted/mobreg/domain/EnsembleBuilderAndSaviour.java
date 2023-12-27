package com.jitterted.mobreg.domain;

import java.net.URI;

public class EnsembleBuilderAndSaviour {

    private Ensemble ensemble;
    private ConferenceDetails conferenceDetails;

    public EnsembleBuilderAndSaviour() {
        ensemble = EnsembleFactory.withStartTimeNow();
    }

    public EnsembleBuilderAndSaviour accept(Member member) {
        ensemble.joinAsParticipant(member.getId());
        return this;
    }

    public EnsembleBuilderAndSaviour decline(Member member) {
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

    public EnsembleBuilderAndSaviour named(String name) {
        ensemble.changeNameTo(name);
        return this;
    }

    public EnsembleBuilderAndSaviour id(int id) {
        ensemble.setId(EnsembleId.of(id));
        return this;
    }

    public EnsembleBuilderAndSaviour asCompleted() {
        ensemble.complete();
        return this;
    }

    public EnsembleBuilderAndSaviour asCanceled() {
        ensemble.cancel();
        return this;
    }

    public EnsembleBuilderAndSaviour withConferenceDetails(String meetingId, String startUrl, String joinUrl) {
        conferenceDetails = new ConferenceDetails(meetingId, URI.create(startUrl), URI.create(joinUrl));
        return this;
    }
}
