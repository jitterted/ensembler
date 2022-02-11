package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EnsembleServiceCancelTest {

    @Test
    public void canceledEnsembleIsSavedInRepositoryAsCanceled() throws Exception {
        Ensemble ensemble = new EnsembleBuilderAndSaviour().build();
        TestEnsembleServiceBuilder ensembleServiceBuilder = new TestEnsembleServiceBuilder()
                .saveEnsemble(ensemble);
        EnsembleId ensembleId = ensembleServiceBuilder.lastSavedEnsembleId();
        EnsembleRepository ensembleRepository = ensembleServiceBuilder.ensembleRepository();
        EnsembleService ensembleService = ensembleServiceBuilder.build();

        ensembleService.cancel(ensembleId);

        Ensemble foundEnsemble = ensembleRepository.findById(ensembleId).get();
        assertThat(foundEnsemble.isCanceled())
                .isTrue();
    }

}