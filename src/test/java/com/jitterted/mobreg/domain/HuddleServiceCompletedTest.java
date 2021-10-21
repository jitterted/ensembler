package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class HuddleServiceCompletedTest {

    @Test
    public void completedHuddleIsCompletedWithLinkAndSavedInRepository() throws Exception {
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(huddleRepository);
        Huddle huddle = new Huddle("Completed", ZonedDateTime.now());
        HuddleId huddleId = huddleRepository.save(huddle).getId();
        huddleRepository.resetSaveCount();

        huddleService.completeWith(huddleId, "https://recording.link/abc987");

        Optional<Huddle> foundHuddle = huddleRepository.findById(huddleId);
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