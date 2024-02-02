package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.application.TestEnsembleServiceBuilder;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleTimerControllerTest {

    @Test
    void gotoTimerSessionForSpecificEnsemble() {
        TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                .saveEnsemble(new Ensemble("Timer", ZonedDateTime.now()));
        EnsembleTimerHolder ensembleTimerHolder = new EnsembleTimerHolder(builder.ensembleRepository());
        EnsembleTimerController ensembleTimerController = new EnsembleTimerController(ensembleTimerHolder);
        EnsembleId ensembleId = builder.lastSavedEnsembleId();

        String redirectPage = ensembleTimerController.gotoTimerView(ensembleId.id());

        assertThat(redirectPage)
                .isEqualTo("redirect:/admin/timer-view");
        assertThat(ensembleTimerHolder.hasTimerFor(ensembleId))
                .isTrue();
    }
}