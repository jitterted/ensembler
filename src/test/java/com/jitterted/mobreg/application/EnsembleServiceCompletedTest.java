package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.InMemoryHuddleRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class EnsembleServiceCompletedTest {

    @Test
    public void completedHuddleIsCompletedWithLinkAndSavedInRepository() throws Exception {
        InMemoryHuddleRepository ensembleRepository = new InMemoryHuddleRepository();
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository);
        Ensemble ensemble = new Ensemble("Completed", ZonedDateTime.now());
        EnsembleId ensembleId = ensembleRepository.save(ensemble).getId();
        ensembleRepository.resetSaveCount();

        ensembleService.completeWith(ensembleId, "https://recording.link/abc987");

        Optional<Ensemble> foundHuddle = ensembleRepository.findById(ensembleId);
        assertThat(foundHuddle)
                .isPresent();
        assertThat(foundHuddle.get().isCompleted())
                .isTrue();
        assertThat(foundHuddle.get().recordingLink().toString())
                .isEqualTo("https://recording.link/abc987");

        assertThat(ensembleRepository.saveCount())
                .isEqualTo(1);
    }
}