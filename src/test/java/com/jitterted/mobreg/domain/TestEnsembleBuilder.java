package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.application.MemberBuilder;
import com.jitterted.mobreg.application.MemberService;

public class TestEnsembleBuilder {

    private Ensemble ensemble;
    private final MemberBuilder memberBuilder;

    public TestEnsembleBuilder() {
        ensemble = EnsembleFactory.withStartTimeNow();
        memberBuilder = new MemberBuilder();
    }

    public TestEnsembleBuilder acceptedMemberWithEmailOf(String recipient) {
        ensemble.acceptedBy(memberBuilder.withEmail(recipient).build().getId());
        return this;
    }

    public TestEnsembleBuilder declinedMemberWithEmailOf(String recipient) {
        ensemble.declinedBy(memberBuilder.withEmail(recipient).build().getId());
        return this;
    }

    public Ensemble build() {
        Ensemble ensembleToReturn = ensemble;
        ensemble = EnsembleFactory.withStartTimeNow();
        return ensembleToReturn;
    }

    public MemberService memberService() {
        return memberBuilder.memberService();
    }
}
