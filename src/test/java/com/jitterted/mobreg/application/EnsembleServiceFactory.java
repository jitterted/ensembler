package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.DummyNotifier;
import com.jitterted.mobreg.application.port.DummyVideoConferenceScheduler;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.application.port.VideoConferenceScheduler;
import org.jetbrains.annotations.NotNull;

public class EnsembleServiceFactory {

    @NotNull
    public static EnsembleService createServiceWith(EnsembleRepository ensembleRepository) {
        return new EnsembleService(ensembleRepository, new InMemoryMemberRepository(),
                                   new DummyNotifier(), new DummyVideoConferenceScheduler());
    }

    @NotNull
    public static EnsembleService createServiceWith(EnsembleRepository ensembleRepository, MemberRepository memberRepository) {
        return new EnsembleService(ensembleRepository, memberRepository, new DummyNotifier(), new DummyVideoConferenceScheduler());
    }

    @NotNull
    public static EnsembleService withDefaults() {
        return createServiceWith(new InMemoryEnsembleRepository());
    }

    @NotNull
    public static EnsembleService with(VideoConferenceScheduler videoConferenceScheduler) {
        return new EnsembleService(new InMemoryEnsembleRepository(), new InMemoryMemberRepository(), new DummyNotifier(), videoConferenceScheduler);
    }

}
