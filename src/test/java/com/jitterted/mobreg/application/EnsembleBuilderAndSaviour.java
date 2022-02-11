package com.jitterted.mobreg.application;

import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleFactory;
import com.jitterted.mobreg.domain.EnsembleId;

public class EnsembleBuilderAndSaviour {

    private Ensemble ensemble;

    public EnsembleBuilderAndSaviour() {
        ensemble = EnsembleFactory.withStartTimeNow();
    }

    public EnsembleBuilderAndSaviour accept(TestMemberBuilder memberBuilder) {
        ensemble.acceptedBy(memberBuilder.buildAndSave().getId());
        return this;
    }

    public EnsembleBuilderAndSaviour decline(TestMemberBuilder memberBuilder) {
        ensemble.declinedBy(memberBuilder.buildAndSave().getId());
        return this;
    }

    public Ensemble build() {
        Ensemble ensembleToReturn = ensemble;
        ensemble = EnsembleFactory.withStartTimeNow();
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

    public EnsembleBuilderAndSaviour completed() {
        ensemble.complete();
        return this;
    }

    public EnsembleBuilderAndSaviour cancel() {
        ensemble.cancel();
        return this;
    }
}
