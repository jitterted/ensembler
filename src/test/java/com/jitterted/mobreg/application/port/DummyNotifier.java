package com.jitterted.mobreg.application.port;

import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;

import java.net.URI;

public class DummyNotifier implements Notifier {
    @Override
    public int ensembleScheduled(Ensemble ensemble, URI registrationLink) {
        return 0;
    }

    @Override
    public void memberAccepted(Ensemble ensemble, Member member) {

    }

    @Override
    public void ensembleCompleted(Ensemble ensemble) {

    }
}
