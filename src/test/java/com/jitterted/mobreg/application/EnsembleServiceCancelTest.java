package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.VideoConferenceScheduler;
import com.jitterted.mobreg.domain.ConferenceDetails;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleBuilderAndSaviour;
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
        EnsembleRepository ensembleRepository = ensembleServiceBuilder.withEnsembleRepository();
        EnsembleService ensembleService = ensembleServiceBuilder.build();

        ensembleService.cancel(ensembleId);

        Ensemble foundEnsemble = ensembleRepository.findById(ensembleId).get();
        assertThat(foundEnsemble.isCanceled())
                .isTrue();
    }

    @Test
    public void canceledEnsembleDeletesVideoConferenceMeeting() throws Exception {
        Ensemble ensemble = new EnsembleBuilderAndSaviour()
                .withConferenceDetails("zoomMeetingId", "https://start.link", "https://join.link")
                .build();
        VideoConferenceScheduler succeedsIfMeetingIdMatchesEnsemble =
                new ZoomConferenceSchedulerDeletesExpectedMeetingId("zoomMeetingId");
        TestEnsembleServiceBuilder ensembleServiceBuilder =
                new TestEnsembleServiceBuilder()
                        .withVideoConferenceScheduler(succeedsIfMeetingIdMatchesEnsemble)
                        .saveEnsemble(ensemble);
        EnsembleId ensembleId = ensembleServiceBuilder.lastSavedEnsembleId();
        EnsembleService ensembleService = ensembleServiceBuilder.build();

        ensembleService.cancel(ensembleId);

        assertThat(ensemble.conferenceDetails())
                .isEqualTo(ConferenceDetails.DELETED);
    }

    @Test
    public void attemptingToCancelEnsembleWithNoMeetingIdDoesNotCallConferenceScheduler() throws Exception {
        DeleteMeetingConferenceSchedulerMock deleteMeetingConferenceSchedulerMock = new DeleteMeetingConferenceSchedulerMock();
        TestEnsembleServiceBuilder ensembleServiceBuilder =
                new TestEnsembleServiceBuilder()
                        .withVideoConferenceScheduler(deleteMeetingConferenceSchedulerMock)
                        .saveEnsemble(new EnsembleBuilderAndSaviour().build());
        EnsembleId ensembleId = ensembleServiceBuilder.lastSavedEnsembleId();
        EnsembleService ensembleService = ensembleServiceBuilder.build();

        ensembleService.cancel(ensembleId);
        
        deleteMeetingConferenceSchedulerMock.verifyDeleteMeetingWasNotCalled();
    }

}