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
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(huddleRepository);
        Ensemble ensemble = new Ensemble("Completed", ZonedDateTime.now());
        EnsembleId ensembleId = huddleRepository.save(ensemble).getId();
        huddleRepository.resetSaveCount();

        huddleService.completeWith(ensembleId, "https://recording.link/abc987");

        Optional<Ensemble> foundHuddle = huddleRepository.findById(ensembleId);
        assertThat(foundHuddle)
                .isPresent();
        assertThat(foundHuddle.get().isCompleted())
                .isTrue();
        assertThat(foundHuddle.get().recordingLink().toString())
                .isEqualTo("https://recording.link/abc987");

        assertThat(huddleRepository.saveCount())
                .isEqualTo(1);
    }
}