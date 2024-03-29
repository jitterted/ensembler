package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.application.TestEnsembleServiceBuilder;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleFactory;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class ParticipantsTransformerTest {

    @Test
    void transformerCreatesMapOfRolesToFirstNamesNoParticipantsRole() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNowAndIdOf(371);
        TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                .saveEnsemble(ensemble)
                .saveMemberAndAccept("Jane", "nextdriver")
                .saveMemberAndAccept("Paul", "driver")
                .saveMemberAndAccept("Sally", "navigator");
        EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(builder.ensembleRepository(), builder.memberRepository());
        ensembleTimerHolder.createTimerFor(EnsembleId.of(371));
        EnsembleTimer ensembleTimer = ensembleTimerHolder.timerFor(EnsembleId.of(371));

        Map<String, Object> rolesToNames = ParticipantsTransformer.participantRolesToNames(ensembleTimer);

        assertThat(rolesToNames)
                .containsOnly(entry("driver", "Paul"),
                              entry("navigator", "Sally"),
                              entry("nextDriver", "Jane"),
                              entry("restOfParticipants", Collections.emptyList())
                );
    }

    @Test
    void transformerCreatesMapOfRolesToFirstNamesWithParticipantsRole() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNowAndIdOf(543);
        TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                .saveEnsemble(ensemble)
                .saveMemberAndAccept("Jane", "nextdriver")
                .saveMemberAndAccept("Paul", "driver")
                .saveMemberAndAccept("Sally", "navigator")
                .saveMemberAndAccept("Sri", "sri_participant")
                .saveMemberAndAccept("Jha", "jha_participant");
        EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(builder.ensembleRepository(), builder.memberRepository());
        ensembleTimerHolder.createTimerFor(EnsembleId.of(543));
        EnsembleTimer ensembleTimer = ensembleTimerHolder.timerFor(EnsembleId.of(543));

        Map<String, Object> rolesToNames = ParticipantsTransformer
                .participantRolesToNames(ensembleTimer);

        assertThat(rolesToNames)
                .containsOnly(entry("driver", "Paul"),
                              entry("navigator", "Sally"),
                              entry("nextDriver", "Jane"),
                              entry("restOfParticipants", List.of("Sri", "Jha"))
                );
    }
}