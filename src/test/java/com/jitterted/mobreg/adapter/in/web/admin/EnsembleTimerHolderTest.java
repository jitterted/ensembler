package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.DoNothingSecondsTicker;
import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.application.EnsembleTimerTickHandler;
import com.jitterted.mobreg.application.TestEnsembleServiceBuilder;
import com.jitterted.mobreg.application.TestMemberBuilder;
import com.jitterted.mobreg.application.port.Broadcaster;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.SecondsTicker;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleBuilder;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.TimeRemaining;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class EnsembleTimerHolderTest {

    private static final Broadcaster DUMMY_BROADCASTER = ensembleTimer -> {
    };

    @Test
    void newTimerHolderHasNoTimerForId() {
        EnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();

        EnsembleTimerHolder ensembleTimerHolder = new EnsembleTimerHolder(ensembleRepository, DUMMY_BROADCASTER, new DoNothingSecondsTicker());

        assertThat(ensembleTimerHolder.hasTimerFor(EnsembleId.of(62)))
                .isFalse();
    }

    @Test
    void existingTimerIsReturnedWhenHolderHasTimerForSpecificEnsemble() {
        Fixture fixture = createEnsembleRepositoryWithEnsembleHavingParticipants(EnsembleId.of(63));
        EnsembleTimerHolder ensembleTimerHolder = new EnsembleTimerHolder(fixture.ensembleRepository(), DUMMY_BROADCASTER, new DoNothingSecondsTicker());
        EnsembleTimer createdEnsemblerTimer = ensembleTimerHolder.createTimerFor(EnsembleId.of(63));

        EnsembleTimer foundEnsembleTimer = ensembleTimerHolder.timerFor(EnsembleId.of(63));

        assertThat(foundEnsembleTimer)
                .isSameAs(createdEnsemblerTimer);
    }

    @Nested
    class Ticker {

        @Test
        void startTimerStartsSecondsTicker() {
            TimerFixture fixture = createTimerFixture(679);
            fixture.ensembleTimerHolder().startTimerFor(EnsembleId.of(679), Instant.now());

            fixture.mockSecondsTicker().verifyStartWasCalledFor(679);

            assertThat(fixture.ensembleTimer().state())
                    .isEqualByComparingTo(EnsembleTimer.TimerState.RUNNING);
        }

        @Test
        void timerRunningNotFinishedDoesNotStopTicker() {
            TimerFixture fixture = createTimerFixture(63);
            Instant timerStartedAt = Instant.now();
            fixture.ensembleTimerHolder().startTimerFor(EnsembleId.of(63), timerStartedAt);

            fixture.ensembleTimerHolder().handleTickFor(EnsembleId.of(63),
                                                        timerStartedAt
                                                                .plus(EnsembleTimer.DEFAULT_TIMER_DURATION)
                                                                .minusSeconds(1));

            fixture.mockSecondsTicker().verifyStopNotCalled();
        }

        @Test
        void timerFinishedStopsSecondsTicker() {
            TimerFixture fixture = createTimerFixture(235);
            Instant timerStartedAt = Instant.now();
            fixture.ensembleTimerHolder().startTimerFor(EnsembleId.of(235), timerStartedAt);

            fixture.ensembleTimerHolder().handleTickFor(EnsembleId.of(235),
                                                        timerStartedAt
                                                                .plus(EnsembleTimer.DEFAULT_TIMER_DURATION));

            fixture.mockSecondsTicker().verifyStartThenStopWasCalledFor(235);
        }

        private TimerFixture createTimerFixture(int ensembleId) {
            Ensemble ensemble = new EnsembleBuilder().id(ensembleId)
                                                     .startsNow()
                                                     .build();
            TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                    .saveEnsemble(ensemble)
                    .withThreeParticipants();
            MockSecondsTicker mockSecondsTicker = new MockSecondsTicker();
            EnsembleTimerHolder ensembleTimerHolder = new EnsembleTimerHolder(builder.ensembleRepository(), DUMMY_BROADCASTER, mockSecondsTicker);
            EnsembleTimer ensembleTimer = ensembleTimerHolder.createTimerFor(EnsembleId.of(ensembleId));
            return new TimerFixture(mockSecondsTicker, ensembleTimerHolder, ensembleTimer);
        }

        private record TimerFixture(MockSecondsTicker mockSecondsTicker,
                                    EnsembleTimerHolder ensembleTimerHolder,
                                    EnsembleTimer ensembleTimer) {
        }

    }

    @Nested
    class UnhappyScenarios {

        @Test
        void whenNoTimerExistsForEnsembleExceptionIsThrown() {
            Fixture fixture = createEnsembleRepositoryWithEnsembleHavingParticipants(EnsembleId.of(77));
            EnsembleTimerHolder ensembleTimerHolder = new EnsembleTimerHolder(fixture.ensembleRepository(), DUMMY_BROADCASTER, new DoNothingSecondsTicker());

            assertThatIllegalStateException()
                    .isThrownBy(() -> ensembleTimerHolder.timerFor(EnsembleId.of(77)))
                    .withMessage("No Ensemble Timer exists for Ensemble 77.");
        }

        @Test
        void askingTimerStartedThrowsExceptionIfTimerDoesNotExistForEnsemble() {
            EnsembleTimerHolder ensembleTimerHolder = new EnsembleTimerHolder(new InMemoryEnsembleRepository(), DUMMY_BROADCASTER, new DoNothingSecondsTicker());

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> ensembleTimerHolder.isTimerRunningFor(EnsembleId.of(444)))
                    .withMessage("No timer for Ensemble ID 444 exists.");
        }

        @Test
        void startTimerThrowsExceptionIfTimerDoesNotExistForEnsemble() {
            EnsembleTimerHolder ensembleTimerHolder = new EnsembleTimerHolder(new InMemoryEnsembleRepository(), DUMMY_BROADCASTER, new DoNothingSecondsTicker());

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> ensembleTimerHolder.startTimerFor(EnsembleId.of(333), Instant.now()))
                    .withMessage("No timer for Ensemble ID 333 exists.");
        }

    }

    @Nested
    class BroadcastEvents {

        @Test
        void onTickWhileRunningBroadcastsCurrentTimerState() {
            BroadcastFixture fixture = createBroadcastFixture(515,
                                                              EnsembleTimer.TimerState.RUNNING,
                                                              new TimeRemaining(3, 0, 75.0));

            fixture.ensembleTimerHolder()
                   .handleTickFor(EnsembleId.of(515), fixture.timerStartedAt().plusSeconds(60));

            fixture.mockBroadcaster().verifyTimerStateSent();
        }

        @Test
        void onTickWhenFinishedBroadcastsTimerFinished() {
            BroadcastFixture fixture = createBroadcastFixture(737,
                                                              EnsembleTimer.TimerState.FINISHED,
                                                              new TimeRemaining(0, 0, 0));

            fixture.ensembleTimerHolder()
                   .handleTickFor(EnsembleId.of(737),
                                  fixture.timerStartedAt()
                                         .plus(EnsembleTimer.DEFAULT_TIMER_DURATION));

            fixture.mockBroadcaster().verifyTimerStateSent();
        }

        @Test
        void onTimerCreationBroadcastsTimerWaitingToStart() {
            BroadcastFixture broadcastFixture = createBroadcastFixture(475,
                                                                       EnsembleTimer.TimerState.WAITING_TO_START,
                                                                       new TimeRemaining(4, 0, 100));

            broadcastFixture.ensembleTimerHolder().createTimerFor(EnsembleId.of(475));

            broadcastFixture.mockBroadcaster().verifyTimerStateSent();
        }

        @Test
        void finishedTimerOnRotateThenTimerMovesToWaitingToStart() {
            BroadcastFixture broadcastFixture = createBroadcastFixture(
                    873,
                    EnsembleTimer.TimerState.WAITING_TO_START,
                    new TimeRemaining(4, 0, 100));
            EnsembleTimer ensembleTimer = broadcastFixture.ensembleTimerHolder()
                                                          .timerFor(EnsembleId.of(873));
            ensembleTimer.tick(broadcastFixture.timerStartedAt()
                                               .plus(EnsembleTimer.DEFAULT_TIMER_DURATION));

            broadcastFixture.ensembleTimerHolder().rotateTimerFor(EnsembleId.of(873));

            broadcastFixture.mockBroadcaster().verifyTimerStateSent();
        }

        @Test
        @Disabled("TODO")
            // @TODO: composite interface of Test and Disabled with Reason
        void onEnsembleEndedRemoveAssociatedTimer() {

        }

        // -- FIXTURE SETUP

        private BroadcastFixture createBroadcastFixture(int ensembleId, EnsembleTimer.TimerState expectedTimerState, TimeRemaining expectedTimeRemaining) {
            Ensemble ensemble = new EnsembleBuilder().id(ensembleId)
                                                     .startsNow()
                                                     .build();
            TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                    .saveEnsemble(ensemble)
                    .withThreeParticipants();
            MockBroadcaster mockBroadcaster = new MockBroadcaster(ensembleId, expectedTimerState, expectedTimeRemaining);
            EnsembleTimerHolder ensembleTimerHolder = new EnsembleTimerHolder(builder.ensembleRepository(),
                                                                              mockBroadcaster,
                                                                              new DoNothingSecondsTicker());
            ensembleTimerHolder.createTimerFor(EnsembleId.of(ensembleId));
            Instant timerStartedAt = Instant.now();
            ensembleTimerHolder.startTimerFor(EnsembleId.of(ensembleId), timerStartedAt);
            mockBroadcaster.reset();
            return new BroadcastFixture(mockBroadcaster, ensembleTimerHolder, timerStartedAt);
        }

        private record BroadcastFixture(MockBroadcaster mockBroadcaster,
                                        EnsembleTimerHolder ensembleTimerHolder,
                                        Instant timerStartedAt) {
        }

        private static class MockBroadcaster implements Broadcaster {
            private boolean wasCalled;
            private final int expectedEnsembleId;
            private final EnsembleTimer.TimerState expectedTimerState;
            private final TimeRemaining expectedTimeRemaining;
            private EnsembleTimer.TimerState lastState;
            private EnsembleId lastEnsembleId;
            private TimeRemaining lastTimeRemaining;

            public MockBroadcaster(int expectedEnsembleId, EnsembleTimer.TimerState expectedTimerState, TimeRemaining expectedTimeRemaining) {
                this.expectedEnsembleId = expectedEnsembleId;
                this.expectedTimerState = expectedTimerState;
                this.expectedTimeRemaining = expectedTimeRemaining;
            }

            @Override
            public void sendCurrentTimer(EnsembleTimer ensembleTimer) {
                wasCalled = true;
                lastState = ensembleTimer.state();
                lastEnsembleId = ensembleTimer.ensembleId();
                lastTimeRemaining = ensembleTimer.timeRemaining();
            }

            void reset() {
                wasCalled = false;
                lastState = null;
                lastEnsembleId = null;
                lastTimeRemaining = null;
            }

            private void verifyTimerStateSent() {
                assertThat(wasCalled)
                        .as("Expected sendCurrentTimer() to have been called on the Broadcaster")
                        .isTrue();
                assertAll(
                        "Timer State",
                        () -> assertThat(lastState)
                                .isEqualByComparingTo(expectedTimerState),
                        () -> assertThat(lastEnsembleId)
                                .isEqualTo(EnsembleId.of(expectedEnsembleId)),
                        () -> assertThat(lastTimeRemaining)
                                .isEqualTo(expectedTimeRemaining)
                );
            }
        }
    }

    // ---- ENCAPSULATED SETUP

    private static Fixture createEnsembleRepositoryWithEnsembleHavingParticipants(EnsembleId ensembleId) {
        EnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        Ensemble ensemble = new Ensemble("Current", ZonedDateTime.now());
        ensemble.setId(ensembleId);
        List<MemberId> participants = createMembersAndJoinAsParticipant(ensemble);
        ensembleRepository.save(ensemble);
        return new Fixture(ensembleRepository, participants);
    }

    private static List<MemberId> createMembersAndJoinAsParticipant(Ensemble ensemble) {
        TestMemberBuilder testMemberBuilder = new TestMemberBuilder();
        List<MemberId> participants = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MemberId firstMemberId = testMemberBuilder.buildAndSave().getId();
            ensemble.joinAsParticipant(firstMemberId);
            participants.add(firstMemberId);
        }
        return participants;
    }

    private record Fixture(EnsembleRepository ensembleRepository, List<MemberId> participants) {
    }


    static class MockSecondsTicker implements SecondsTicker {
        private boolean startWasCalled;
        private long startEnsembleId;
        private boolean stopWasCalled;

        @Override
        public void start(EnsembleId ensembleId, EnsembleTimerTickHandler ensembleTimerTickHandler) {
            startWasCalled = true;
            startEnsembleId = ensembleId.id();
        }

        @Override
        public void stop() {
            stopWasCalled = true;
        }

        void verifyStartWasCalledFor(long ensembleId) {
            assertThat(startWasCalled)
                    .as("Expected SecondsTicker.start() to be called, but was not.")
                    .isTrue();
            assertThat(startEnsembleId)
                    .as("Expected start() to be called with the correct Ensemble ID")
                    .isEqualTo(ensembleId);
        }

        void verifyStartThenStopWasCalledFor(long ensembleId) {
            verifyStartWasCalledFor(ensembleId);
            assertThat(stopWasCalled)
                    .as("Expected SecondsTicker.stop() to be called, but was not.")
                    .isTrue();
        }

        void verifyStopNotCalled() {
            assertThat(stopWasCalled)
                    .as("Expected SecondsTicker.stop() to NOT be called, but it was")
                    .isFalse();
        }
    }

}
