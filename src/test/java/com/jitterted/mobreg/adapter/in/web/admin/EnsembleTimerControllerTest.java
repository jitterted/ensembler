package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.application.TestEnsembleServiceBuilder;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleBuilder;
import com.jitterted.mobreg.domain.EnsembleFactory;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.EnsembleTimerFactory;
import com.jitterted.mobreg.domain.MemberId;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.*;

class EnsembleTimerControllerTest {

    @Test
    void createAndRedirectToTimerSessionForSpecificEnsemble() {
        Ensemble ensemble = new EnsembleBuilder().id(87)
                                                 .startsNow()
                                                 .build();
        TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                .saveEnsemble(ensemble)
                .withThreeParticipants();
        EnsembleTimerHolder ensembleTimerHolder = new EnsembleTimerHolder(builder.ensembleRepository());
        EnsembleTimerController ensembleTimerController = new EnsembleTimerController(ensembleTimerHolder, new InMemoryMemberRepository());

        String redirectPage = ensembleTimerController.createTimerView(87L);

        assertThat(redirectPage)
                .isEqualTo("redirect:/admin/timer-view/87");
        assertThat(ensembleTimerHolder.hasTimerFor(EnsembleId.of(87)))
                .isTrue();
    }

    @Test
    void viewTimerHasParticipantsInTheViewModelFromTheSpecifiedEnsembleAndTimerNotStarted() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNowAndIdAndName(153, "Dolphin Ensemble");
        TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                .saveEnsemble(ensemble)
                .saveMemberAndAccept("Jane", "ghjane")
                .saveMemberAndAccept("Paul", "ghpaul")
                .saveMemberAndAccept("Sally", "ghsally");
        EnsembleTimerHolder ensembleTimerHolder = new EnsembleTimerHolder(builder.ensembleRepository());
        EnsembleTimerController ensembleTimerController = new EnsembleTimerController(ensembleTimerHolder, builder.memberRepository());
        ensembleTimerController.createTimerView(153L);

        Model model = new ConcurrentModel();
        ensembleTimerController.viewTimer(153L, model);
        String viewName = ensembleTimerController.viewTimer(153L, model); // ensure refreshable page

        assertThat(viewName)
                .isEqualTo("ensemble-timer");
        assertThat(model.asMap())
                .containsEntry("ensembleName", "Dolphin Ensemble")
                .extractingByKey("participantNames", InstanceOfAssertFactories.list(String.class))
                .containsExactlyInAnyOrder("Jane", "Sally", "Paul");
        assertThat(ensembleTimerHolder.isTimerRunningFor(EnsembleId.of(153L)))
                .isFalse();
    }

    @Test
    void startTimerStartsTheSpecifiedEnsembleTimer() {
        Ensemble ensemble = new EnsembleBuilder().id(279)
                                                 .startsNow()
                                                 .build();
        TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                .saveEnsemble(ensemble)
                .withThreeParticipants();
        EnsembleTimerHolder ensembleTimerHolder = new EnsembleTimerHolder(builder.ensembleRepository());
        EnsembleTimerController ensembleTimerController = new EnsembleTimerController(ensembleTimerHolder, new InMemoryMemberRepository());
        ensembleTimerController.createTimerView(279L);

        ensembleTimerController.startTimer(279L);

        assertThat(ensembleTimerHolder.isTimerRunningFor(EnsembleId.of(279L)))
                .isTrue();
    }

    @Test
    public void rotateTimerRotatesParticipantsForFinishedEnsembleTimer() throws Exception {
        Ensemble ensemble = new EnsembleBuilder().id(279)
                                                 .startsNow()
                                                 .build();
        TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                .saveEnsemble(ensemble)
                .withThreeParticipants();
        EnsembleTimerHolder ensembleTimerHolder = new EnsembleTimerHolder(builder.ensembleRepository());
        EnsembleTimer ensembleTimer = ensembleTimerHolder.createTimerFor(EnsembleId.of(279));
        MemberId nextDriverBeforeRotation = ensembleTimer.rotation().nextDriver();
        EnsembleTimerFactory.pushTimerToFinishedState(ensembleTimer);
        EnsembleTimerController ensembleTimerController = new EnsembleTimerController(ensembleTimerHolder, new InMemoryMemberRepository());

        ensembleTimerController.rotateTimer(279L);

        assertThat(ensembleTimer.rotation().driver())
                .isEqualTo(nextDriverBeforeRotation);
    }
}