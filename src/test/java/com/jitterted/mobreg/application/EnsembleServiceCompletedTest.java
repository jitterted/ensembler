package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class EnsembleServiceCompletedTest {

    @Test
    public void completedEnsembleIsCompletedWithLinkAndSavedInRepository() throws Exception {
        InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository);
        Ensemble ensemble = new Ensemble("Completed", ZonedDateTime.now());
        EnsembleId ensembleId = ensembleRepository.save(ensemble).getId();
        ensembleRepository.resetSaveCount();

        ensembleService.completeWith(ensembleId, "https://recording.link/abc987");

        Optional<Ensemble> foundEnsemble = ensembleRepository.findById(ensembleId);
        assertThat(foundEnsemble)
                .isPresent();
        assertThat(foundEnsemble.get().isCompleted())
                .isTrue();
        assertThat(foundEnsemble.get().recordingLink().toString())
                .isEqualTo("https://recording.link/abc987");

        assertThat(ensembleRepository.saveCount())
                .isEqualTo(1);
    }
}