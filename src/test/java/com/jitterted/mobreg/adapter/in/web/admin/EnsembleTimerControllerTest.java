package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.application.TestEnsembleServiceBuilder;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleFactory;
import com.jitterted.mobreg.domain.EnsembleId;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.*;

class EnsembleTimerControllerTest {

    @Test
    void createAndRedirectToTimerSessionForSpecificEnsemble() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNowAndIdOf(87);
        TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder().saveEnsemble(ensemble);
        EnsembleTimerHolder ensembleTimerHolder = new EnsembleTimerHolder(builder.ensembleRepository());
        EnsembleTimerController ensembleTimerController = new EnsembleTimerController(ensembleTimerHolder);

        String redirectPage = ensembleTimerController.gotoTimerView(87L);

        assertThat(redirectPage)
                .isEqualTo("redirect:/admin/timer-view/87");
        assertThat(ensembleTimerHolder.hasTimerFor(EnsembleId.of(87)))
                .isTrue();
    }

    @Test
    void viewTimerHasParticipantsInTheViewModelFromTheSpecifiedEnsemble() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNowAndIdAndName(153, "Dolphin Ensemble");
        TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                .saveEnsemble(ensemble)
                .saveMemberAndAccept("Jane", "ghjane")
                .saveMemberAndAccept("Sally", "ghsally")
                .saveMemberAndAccept("Paul", "ghpaul");
        EnsembleTimerHolder ensembleTimerHolder = new EnsembleTimerHolder(builder.ensembleRepository());
        EnsembleTimerController ensembleTimerController = new EnsembleTimerController(ensembleTimerHolder);

        Model model = new ConcurrentModel();
        String viewName = ensembleTimerController.viewTimer(153L, model);

        assertThat(viewName)
                .isEqualTo("ensemble-timer");
        assertThat(model.asMap())
                .containsEntry("ensembleName", "Dolphin Ensemble")
                .extractingByKey("participantNames", InstanceOfAssertFactories.list(String.class))
                .containsExactly("Jane", "Sally", "Paul");
    }

}