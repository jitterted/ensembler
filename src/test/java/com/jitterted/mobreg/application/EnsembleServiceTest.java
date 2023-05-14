package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleFactory;
import com.jitterted.mobreg.domain.MemberId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EnsembleServiceTest {

    @Test
    public void joinAsSpectatorDelegatesToEnsemble() {
        InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        ensembleRepository.save(ensemble);
        ensembleRepository.resetSaveCount();
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository);
        MemberId memberId = MemberId.of(37);

        ensembleService.joinAsSpectator(ensemble.getId(), memberId);

        assertThat(ensembleRepository.savedEnsembles())
                .hasSize(1);
        assertThat(ensembleRepository.savedEnsembles().get(0).spectators())
                .containsExactly(memberId);
    }
}