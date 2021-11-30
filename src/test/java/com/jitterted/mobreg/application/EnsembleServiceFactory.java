package com.jitterted.mobreg.application;

import com.jitterted.mobreg.adapter.in.web.member.DummyNotifier;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;

public class EnsembleServiceFactory {

    public static EnsembleService createServiceWith(EnsembleRepository ensembleRepository) {
        return new EnsembleService(ensembleRepository, new InMemoryMemberRepository(), new DummyNotifier());
    }

    public static EnsembleService createServiceWith(EnsembleRepository ensembleRepository, MemberRepository memberRepository) {
        return new EnsembleService(ensembleRepository, memberRepository, new DummyNotifier());
    }
}
