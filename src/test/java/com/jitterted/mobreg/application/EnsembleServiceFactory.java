package com.jitterted.mobreg.application;

import com.jitterted.mobreg.adapter.in.web.member.DummyNotifier;
import com.jitterted.mobreg.application.port.HuddleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;

public class EnsembleServiceFactory {

    public static EnsembleService createServiceWith(HuddleRepository ensembleRepository) {
        return new EnsembleService(ensembleRepository, new InMemoryMemberRepository(), new DummyNotifier());
    }

    public static EnsembleService createServiceWith(HuddleRepository ensembleRepository, MemberRepository memberRepository) {
        return new EnsembleService(ensembleRepository, memberRepository, new DummyNotifier());
    }
}
