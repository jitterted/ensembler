package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.in.web.admin.ParticipantsTransformer;
import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.application.TestEnsembleServiceBuilder;
import com.jitterted.mobreg.domain.CountdownTimer;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleBuilder;
import com.jitterted.mobreg.domain.EnsembleFactory;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.EnsembleTimerFactory;
import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.*;

class EnsembleTimerControllerTest {

    @Test
    void viewTimerHasParticipantsInTheViewModelFromTheSpecifiedEnsembleAndTimerNotStarted() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNowAndIdAndName(153, "Dolphin Ensemble");
        TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                .saveEnsemble(ensemble)
                .saveMemberAndAccept("Jane", "ghjane")
                .saveMemberAndAccept("Paul", "ghpaul")
                .saveMemberAndAccept("Sally", "ghsally");
        EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(builder.ensembleRepository(), builder.memberRepository());
        ensembleTimerHolder.createTimerFor(EnsembleId.of(153));
        EnsembleTimerController ensembleTimerController = new EnsembleTimerController(ensembleTimerHolder);

        Model model = new ConcurrentModel();
        ensembleTimerController.viewTimer(153L, model);
        String viewName = ensembleTimerController.viewTimer(153L, model); // ensure refreshable page

        assertThat(viewName)
                .isEqualTo("ensemble-timer");
        assertThat(model.asMap())
                .containsEntry("ensembleName", "Dolphin Ensemble")
                .containsAllEntriesOf(
                        ParticipantsTransformer.participantRolesToNames(
                                ensembleTimerHolder.timerFor(EnsembleId.of(153))));
        assertThat(ensembleTimerHolder.isTimerRunningFor(EnsembleId.of(153L)))
                .isFalse();
    }

    @Test
    void startTimerStartsTheSpecifiedEnsembleTimer() {
        Fixture fixture = createEnsembleAndTimerWithIdOf(653);
        fixture.ensembleTimerController().startTimer(653L);

        assertThat(fixture.ensembleTimerHolder().isTimerRunningFor(EnsembleId.of(653L)))
                .isTrue();
    }

    @Test
    public void rotateTimerRotatesParticipantsForFinishedEnsembleTimer() {
        Fixture fixture = createEnsembleAndTimerWithIdOf(279);
        Member nextDriverBeforeRotation = fixture.ensembleTimer().rotation().nextDriver();
        EnsembleTimerFactory.pushTimerToFinishedState(fixture.ensembleTimer());

        fixture.ensembleTimerController().rotateTimer(279L);

        assertThat(fixture.ensembleTimer().rotation().driver())
                .isEqualTo(nextDriverBeforeRotation);
    }

    @Test
    void pauseTimerPausesTheSpecifiedEnsembleTimer() {
        Fixture fixture = createEnsembleAndTimerWithIdOf(743);
        fixture.ensembleTimerController().startTimer(743L);

        fixture.ensembleTimerController().pauseTimer(743L);

        assertThat(fixture.ensembleTimer().state())
                .isEqualByComparingTo(CountdownTimer.TimerState.PAUSED);
    }

    @Test
    void resumePausedTimerResumesTheSpecifiedTimer() {
        Fixture fixture = createEnsembleAndTimerWithIdOf(672);
        fixture.ensembleTimerController().startTimer(672L);
        fixture.ensembleTimerController().pauseTimer(672L);

        fixture.ensembleTimerController().resumeTimer(672L);

        assertThat(fixture.ensembleTimer().state())
                .isEqualByComparingTo(CountdownTimer.TimerState.RUNNING);
    }

    // --- FIXTURES ---

    static Fixture createEnsembleAndTimerWithIdOf(int ensembleId) {
        Ensemble ensemble = new EnsembleBuilder().id(ensembleId)
                                                 .startsNow()
                                                 .build();
        TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                .saveEnsemble(ensemble)
                .withThreeParticipants();
        EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(builder.ensembleRepository(), builder.memberRepository());
        EnsembleTimerController ensembleTimerController = new EnsembleTimerController(ensembleTimerHolder);
        EnsembleTimer ensembleTimer = ensembleTimerHolder.createTimerFor(EnsembleId.of(ensembleId));
        return new Fixture(ensembleTimerController, ensembleTimerHolder, ensembleTimer);
    }

    private record Fixture(EnsembleTimerController ensembleTimerController,
                           EnsembleTimerHolder ensembleTimerHolder,
                           EnsembleTimer ensembleTimer) {
    }
}