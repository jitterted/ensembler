package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.application.EnsembleTimerTickHandler;
import com.jitterted.mobreg.application.NoOpShuffler;
import com.jitterted.mobreg.application.TestEnsembleServiceBuilder;
import com.jitterted.mobreg.application.port.Broadcaster;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.application.port.SecondsTicker;
import com.jitterted.mobreg.domain.CountdownTimer;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleBuilder;
import com.jitterted.mobreg.domain.EnsembleFactory;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.TimeRemaining;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class EnsembleTimerHolderTest {

    @Nested
    class HappyScenarios {
        @Test
        void newTimerHolderHasNoTimerForId() {
            EnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
            MemberRepository memberRepository = new InMemoryMemberRepository();

            EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(ensembleRepository, memberRepository);

            assertThat(ensembleTimerHolder.hasTimerFor(EnsembleId.of(62)))
                    .isFalse();
        }

        @Test
        void existingTimerIsReturnedWhenHolderHasTimerForSpecificEnsemble() {
            EnsembleId ensembleId = EnsembleId.of(63);
            Fixture fixture = createEnsembleRepositoryWithEnsembleHavingParticipants(ensembleId);
            EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(fixture.ensembleRepository(), fixture.memberRepository());
            EnsembleTimer createdEnsemblerTimer = ensembleTimerHolder.createTimerFor(ensembleId, new NoOpShuffler());

            EnsembleTimer foundEnsembleTimer = ensembleTimerHolder.timerFor(ensembleId);

            assertThat(foundEnsembleTimer)
                    .isSameAs(createdEnsemblerTimer);
        }

        @Test
        void createTimerFailsWhenTimerExistsForAnotherEnsemble() {
            int ensembleWithTimerCreatedFirstId = 96;
            Ensemble ensembleWithTimerCreatedFirst =
                    new EnsembleBuilder()
                            .id(ensembleWithTimerCreatedFirstId)
                            .startsNow()
                            .build();
            long ensembleWithTimerCreatedSecondId = 583L;
            Ensemble ensembleWithTimerCreatedSecond =
                    new EnsembleBuilder()
                            .id(ensembleWithTimerCreatedSecondId)
                            .startsNow()
                            .build();
            TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                    .saveEnsemble(ensembleWithTimerCreatedSecond)
                    .withThreeParticipants()
                    .saveEnsemble(ensembleWithTimerCreatedFirst)
                    .withThreeParticipants();
            EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(builder.ensembleRepository(), builder.memberRepository());
            ensembleTimerHolder.createTimerFor(ensembleWithTimerCreatedFirst.getId(), new NoOpShuffler());

            EnsembleTimer timerForSecondEnsemble = ensembleTimerHolder.createTimerFor(ensembleWithTimerCreatedSecond.getId(), new NoOpShuffler());

            assertThat(timerForSecondEnsemble.ensembleId().id())
                    .isEqualTo(ensembleWithTimerCreatedSecondId);
            assertThatIllegalStateException()
                    .isThrownBy(() -> ensembleTimerHolder.timerFor(ensembleWithTimerCreatedFirst.getId()));
        }
    }

    @Nested
    class Ticker {

        @Test
        void startTimerStartsSecondsTicker() {
            TimerFixture fixture = createTimerFixture(679);
            fixture.ensembleTimerHolder()
                   .startTimerFor(EnsembleId.of(679), Instant.now());

            fixture.mockSecondsTicker()
                   .verifyStartWasCalledFor(679);

            assertThat(fixture.ensembleTimer()
                              .state())
                    .isEqualByComparingTo(CountdownTimer.TimerState.RUNNING);
        }

        @Test
        void timerRunningNotFinishedDoesNotStopTicker() {
            TimerFixture fixture = createTimerFixture(63);
            Instant timerStartedAt = Instant.now();
            fixture.ensembleTimerHolder()
                   .startTimerFor(EnsembleId.of(63), timerStartedAt);

            fixture.ensembleTimerHolder()
                   .handleTickFor(EnsembleId.of(63),
                                  timerStartedAt
                                          .plus(EnsembleTimer.DEFAULT_TIMER_DURATION)
                                          .minusSeconds(1));

            fixture.mockSecondsTicker()
                   .verifyStopNotCalled();
        }

        @Test
        void resetTimerThenStateIsWaitingToStartAndTickerStopped() {
            EnsembleId ensembleId = EnsembleId.of(63);
            TimerFixture fixture = createTimerFixture(ensembleId.id());
            Instant timerStartedAt = Instant.now();
            fixture.ensembleTimerHolder()
                   .startTimerFor(ensembleId, timerStartedAt);
            fixture.ensembleTimerHolder()
                   .handleTickFor(ensembleId, timerStartedAt.plusSeconds(3));

            fixture.ensembleTimerHolder()
                   .resetTimerFor(ensembleId);

            fixture.mockSecondsTicker()
                   .verifyStopCalled();

            assertThat(fixture.ensembleTimer()
                              .state())
                    .isEqualByComparingTo(CountdownTimer.TimerState.WAITING_TO_START);
            assertThat(fixture.ensembleTimer()
                              .timeRemaining()
                              .percent())
                    .isEqualTo(100.0);
        }

        @Test
        void timerFinishedStopsSecondsTicker() {
            TimerFixture fixture = createTimerFixture(235);
            Instant timerStartedAt = Instant.now();
            fixture.ensembleTimerHolder()
                   .startTimerFor(EnsembleId.of(235), timerStartedAt);

            fixture.ensembleTimerHolder()
                   .handleTickFor(EnsembleId.of(235),
                                  timerStartedAt
                                          .plus(EnsembleTimer.DEFAULT_TIMER_DURATION));

            fixture.mockSecondsTicker()
                   .verifyStartThenStopWasCalledFor(235);
        }

        @Test
        void timerDeletedStopsSecondsTicker() {
            TimerFixture fixture = createTimerFixture(235);
            fixture.ensembleTimerHolder()
                   .startTimerFor(EnsembleId.of(235), Instant.now());

            fixture.ensembleTimerHolder()
                   .deleteTimer(EnsembleId.of(235));

            fixture.mockSecondsTicker()
                   .verifyStartThenStopWasCalledFor(235);
        }

        @Test
        void secondsTickerStoppedWhenHandleTickForNonExistentTimer() {
            Ensemble ensemble = new EnsembleBuilder().id(637)
                                                     .startsNow()
                                                     .build();
            TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                    .saveEnsemble(ensemble);
            MockSecondsTicker mockSecondsTicker = new MockSecondsTicker();
            EnsembleTimerHolder ensembleTimerHolder =
                    EnsembleTimerHolder.createNull(builder.ensembleRepository(),
                                                   builder.memberRepository(),
                                                   mockSecondsTicker);

            try {
                ensembleTimerHolder.handleTickFor(EnsembleId.of(637), Instant.now());
            } catch (IllegalStateException ise) {
                mockSecondsTicker.verifyStopCalled();
            }
        }

        private TimerFixture createTimerFixture(long ensembleId) {
            Ensemble ensemble = new EnsembleBuilder().id(ensembleId)
                                                     .startsNow()
                                                     .build();
            TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                    .saveEnsemble(ensemble)
                    .withThreeParticipants();
            MockSecondsTicker mockSecondsTicker = new MockSecondsTicker();
            EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(builder.ensembleRepository(),
                                                                                     builder.memberRepository(),
                                                                                     mockSecondsTicker);
            EnsembleTimer ensembleTimer = ensembleTimerHolder.createTimerFor(EnsembleId.of(ensembleId), new NoOpShuffler());
            return new TimerFixture(mockSecondsTicker, ensembleTimerHolder, ensembleTimer);
        }

        private record TimerFixture(MockSecondsTicker mockSecondsTicker,
                                    EnsembleTimerHolder ensembleTimerHolder,
                                    EnsembleTimer ensembleTimer) {
        }

    }

    @Nested
    class UnhappyScenarios_ExceptionThrownWhen {

        @Test
        void askForTimerButNoTimerExists() {
            Fixture fixture = createEnsembleRepositoryWithEnsembleHavingParticipants(EnsembleId.of(77));
            EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(fixture.ensembleRepository(), fixture.memberRepository());

            assertThatIllegalStateException()
                    .isThrownBy(() -> ensembleTimerHolder.timerFor(EnsembleId.of(77)))
                    .withMessage("No Ensemble Timer exists for Ensemble 77.");
        }

        @Test
        void queryIfTimerStartedButNoTimerExists() {
            EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(new InMemoryEnsembleRepository(), new InMemoryMemberRepository());

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> ensembleTimerHolder.isTimerRunningFor(EnsembleId.of(444)))
                    .withMessage("No timer for Ensemble ID 444 exists.");
        }

        @Test
        void startTimerButNoTimerExists() {
            EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(new InMemoryEnsembleRepository(), new InMemoryMemberRepository());

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> ensembleTimerHolder.startTimerFor(EnsembleId.of(333), Instant.now()))
                    .withMessage("No timer for Ensemble ID 333 exists.");
        }

    }

    @Nested
    class BroadcastEvents {
        @Test
        void onTimerCreationBroadcastsTimerWaitingToStart() {
            BroadcastFixture fixture = createBroadcasterWithStartedEnsembleTimer(475,
                                                                                 CountdownTimer.TimerState.WAITING_TO_START,
                                                                                 new TimeRemaining(4, 0, 100));

            fixture.ensembleTimerHolder()
                   .createTimerFor(EnsembleId.of(475), new NoOpShuffler());

            fixture.mockBroadcaster()
                   .verifyTimerStateSent();
        }

        @Test
        void onTickWhileRunningBroadcastsCurrentTimerState() {
            BroadcastFixture fixture = createBroadcasterWithStartedEnsembleTimer(
                    515,
                    CountdownTimer.TimerState.RUNNING,
                    TimeRemaining.from(Duration.ofMinutes(3), Duration.ofMinutes(4)));

            fixture.tickAfterStart(Duration.ofSeconds(60));

            fixture.mockBroadcaster()
                   .verifyTimerStateSent();
        }

        @Test
        void onTimerPauseBroadcastsPauseStateAndPausedEvent() {
            BroadcastFixture fixture = createBroadcasterWithStartedEnsembleTimer(
                    458,
                    CountdownTimer.TimerState.PAUSED,
                    TimeRemaining.from(Duration.ofMinutes(2), Duration.ofMinutes(4)));
            fixture.tickAfterStart(Duration.ofMinutes(2));
            fixture.mockBroadcaster()
                   .reset();

            fixture.ensembleTimerHolder()
                   .pauseTimerFor(EnsembleId.of(458));

            fixture.mockBroadcaster()
                   .verifyTimerStateSent();
            fixture.mockBroadcaster()
                   .verifyPausedEventSent();
        }

        @Test
        void onTimerResumeBroadcastsRunningStateAndSendsResumedEvent() {
            BroadcastFixture fixture = createBroadcasterWithStartedEnsembleTimer(
                    218,
                    CountdownTimer.TimerState.RUNNING,
                    TimeRemaining.from(Duration.ofMinutes(2), Duration.ofMinutes(4)));
            fixture.tickAfterStart(Duration.ofMinutes(2));
            fixture.ensembleTimerHolder()
                   .pauseTimerFor(EnsembleId.of(218));
            fixture.mockBroadcaster()
                   .reset();

            fixture.ensembleTimerHolder()
                   .resumeTimerFor(EnsembleId.of(218));

            fixture.mockBroadcaster()
                   .verifyTimerStateSent();
            fixture.mockBroadcaster()
                   .verifyResumedEventSent();
        }

        @Test
        void onTickWhenFinishedBroadcastsTimerFinishedWithFinishedEvent() {
            BroadcastFixture fixture = createBroadcasterWithStartedEnsembleTimer(
                    737,
                    CountdownTimer.TimerState.FINISHED,
                    new TimeRemaining(0, 0, 0));

            fixture.tickAfterStart(EnsembleTimer.DEFAULT_TIMER_DURATION);

            fixture.mockBroadcaster()
                   .verifyTimerStateSent();
            fixture.mockBroadcaster()
                   .verifyFinishedEventSent();
        }

        @Test
        void finishedTimerOnRotateThenTimerMovesToWaitingToStart() {
            BroadcastFixture broadcastFixture = createBroadcasterWithStartedEnsembleTimer(
                    873,
                    CountdownTimer.TimerState.WAITING_TO_START,
                    new TimeRemaining(4, 0, 100));
            EnsembleTimer ensembleTimer = broadcastFixture.ensembleTimerHolder()
                                                          .timerFor(EnsembleId.of(873));
            ensembleTimer.tick(broadcastFixture.timerStartedAt()
                                               .plus(EnsembleTimer.DEFAULT_TIMER_DURATION));

            broadcastFixture.ensembleTimerHolder()
                            .rotateTimerFor(EnsembleId.of(873));

            broadcastFixture.mockBroadcaster()
                            .verifyTimerStateSent();
        }

        @Test
        void resetTimerBroadcastsTimerWaitingToStart() {
            BroadcastFixture broadcastFixture = createBroadcasterWithStartedEnsembleTimer(
                    571,
                    CountdownTimer.TimerState.WAITING_TO_START,
                    new TimeRemaining(4, 0, 100));

            broadcastFixture.ensembleTimerHolder()
                            .resetTimerFor(EnsembleId.of(571));

            broadcastFixture.mockBroadcaster()
                            .verifyTimerStateSent();
        }

        @Test
        @Disabled("TODO")
            // @TODO: composite interface of Test and Disabled with Reason
        void onEnsembleEndedRemoveAssociatedTimer() {

        }

        // -- FIXTURE SETUP

        private BroadcastFixture createBroadcasterWithStartedEnsembleTimer(int ensembleId, CountdownTimer.TimerState expectedTimerState, TimeRemaining expectedTimeRemaining) {
            Ensemble ensemble = new EnsembleBuilder().id(ensembleId)
                                                     .startsNow()
                                                     .build();
            TestEnsembleServiceBuilder builder =
                    new TestEnsembleServiceBuilder()
                            .saveEnsemble(ensemble)
                            .withThreeParticipants();
            MockBroadcaster mockBroadcaster = new MockBroadcaster(ensembleId, expectedTimerState, expectedTimeRemaining);
            EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(builder.ensembleRepository(),
                                                                                     builder.memberRepository(),
                                                                                     mockBroadcaster);
            ensembleTimerHolder.createTimerFor(EnsembleId.of(ensembleId), new NoOpShuffler());
            Instant timerStartedAt = Instant.now();
            ensembleTimerHolder.startTimerFor(EnsembleId.of(ensembleId), timerStartedAt);
            mockBroadcaster.reset();
            return new BroadcastFixture(mockBroadcaster, ensembleTimerHolder, ensembleId, timerStartedAt);
        }

        private record BroadcastFixture(MockBroadcaster mockBroadcaster,
                                        EnsembleTimerHolder ensembleTimerHolder,
                                        int ensembleId,
                                        Instant timerStartedAt) {
            private void tickAfterStart(Duration durationAfterStartToTick) {
                ensembleTimerHolder()
                        .handleTickFor(EnsembleId.of(ensembleId), timerStartedAt.plus(durationAfterStartToTick));
            }
        }

        private static class MockBroadcaster implements Broadcaster {
            private boolean currentTimerWasSent;
            private final int expectedEnsembleId;
            private final CountdownTimer.TimerState expectedTimerState;
            private final TimeRemaining expectedTimeRemaining;
            private CountdownTimer.TimerState lastState;
            private EnsembleId lastEnsembleId;
            private TimeRemaining lastTimeRemaining;
            private EnsembleTimer.TimerEvent lastEventSent;

            public MockBroadcaster(int expectedEnsembleId, CountdownTimer.TimerState expectedTimerState, TimeRemaining expectedTimeRemaining) {
                this.expectedEnsembleId = expectedEnsembleId;
                this.expectedTimerState = expectedTimerState;
                this.expectedTimeRemaining = expectedTimeRemaining;
            }

            @Override
            public void sendCurrentTimer(EnsembleTimer ensembleTimer) {
                currentTimerWasSent = true;
                lastState = ensembleTimer.state();
                lastEnsembleId = ensembleTimer.ensembleId();
                lastTimeRemaining = ensembleTimer.timeRemaining();
            }

            @Override
            public void sendEvent(EnsembleTimer.TimerEvent timerEvent) {
                lastEventSent = timerEvent;
            }

            void reset() {
                currentTimerWasSent = false;
                lastState = null;
                lastEnsembleId = null;
                lastTimeRemaining = null;
                lastEventSent = null;
            }

            private void verifyTimerStateSent() {
                assertThat(currentTimerWasSent)
                        .as("Expected sendCurrentTimer(%s) to have been called on the Broadcaster", expectedTimerState)
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

            public void verifyFinishedEventSent() {
                verifyLastEventSent(EnsembleTimer.TimerEvent.FINISHED);
            }

            public void verifyPausedEventSent() {
                verifyLastEventSent(EnsembleTimer.TimerEvent.PAUSED);
            }

            public void verifyResumedEventSent() {
                verifyLastEventSent(EnsembleTimer.TimerEvent.RESUMED);
            }

            private void verifyLastEventSent(EnsembleTimer.TimerEvent timerEvent) {
                assertThat(lastEventSent)
                        .as("Expected last sendEvent() to be called with " + timerEvent + ", but wasn't.")
                        .isNotNull()
                        .isEqualByComparingTo(timerEvent);
            }
        }
    }

    // ---- ENCAPSULATED SETUP

    private static Fixture createEnsembleRepositoryWithEnsembleHavingParticipants(EnsembleId ensembleId) {
        Ensemble ensemble = EnsembleFactory.withStartTimeNowAndIdOf(ensembleId.id());
        TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                .saveEnsemble(ensemble)
                .withThreeParticipants();
        return new Fixture(builder.ensembleRepository(),
                           builder.memberRepository(),
                           ensemble.participants()
                                   .map(memberId -> builder.memberRepository()
                                                           .findById(memberId)
                                                           .orElseThrow())
                                   .toList()
        );
    }

    private record Fixture(EnsembleRepository ensembleRepository,
                           MemberRepository memberRepository,
                           List<Member> participants) {
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

        public void verifyStopCalled() {
            assertThat(stopWasCalled)
                    .as("Expected SecondsTicker.stop() to be called, but it was NOT called")
                    .isTrue();
        }
    }

}
