package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Test;

import java.net.URI;
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

    @Test
    public void completedEnsembleNotifiesAcceptedMembers() throws Exception {
        MockEnsembleCompletedNotifier mockEnsembleCompletedNotifier = new MockEnsembleCompletedNotifier();
        TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                .notifier(mockEnsembleCompletedNotifier)
                .saveEnsembleStartingNow("test")
                .saveMemberAndAccept("Ace", "accepterino");
        EnsembleService ensembleService = builder.build();

        ensembleService.completeWith(builder.lastSavedEnsembleId(), "https://recording.link/123");

        mockEnsembleCompletedNotifier.verify();
    }


    private static class MockEnsembleCompletedNotifier implements Notifier {
        private boolean ensembleCompletedCalled;

        @Override
        public int ensembleScheduled(Ensemble ensemble, URI registrationLink) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void memberAccepted(Ensemble ensemble, Member member) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void ensembleCompleted(Ensemble ensemble) {
            ensembleCompletedCalled = true;
        }

        public void verify() {
            assertThat(ensembleCompletedCalled)
                    .describedAs("Ensemble Completed was never called.")
                    .isTrue();
        }
    }


}