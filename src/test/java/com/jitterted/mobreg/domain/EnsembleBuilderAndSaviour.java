package com.jitterted.mobreg.domain;

public class EnsembleBuilderAndSaviour {

    private Ensemble ensemble;

    public EnsembleBuilderAndSaviour() {
        ensemble = EnsembleFactory.withStartTimeNow();
    }

    public EnsembleBuilderAndSaviour accept(Member member) {
        ensemble.acceptedBy(member.getId());
        return this;
    }

    public EnsembleBuilderAndSaviour decline(Member member) {
        ensemble.declinedBy(member.getId());
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

    public EnsembleBuilderAndSaviour asCompleted() {
        ensemble.complete();
        return this;
    }

    public EnsembleBuilderAndSaviour asCanceled() {
        ensemble.cancel();
        return this;
    }
}
