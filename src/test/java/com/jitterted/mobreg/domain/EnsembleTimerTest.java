package com.jitterted.mobreg.domain;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class EnsembleTimerTest {

    @Nested
    class TimerStates {
        @Test
        void newTimerIsWaitingToStartHasFullTimeRemaining() {
            EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimer();

            assertThat(ensembleTimer.state())
                    .isEqualByComparingTo(EnsembleTimer.TimerState.WAITING_TO_START);
            assertThat(ensembleTimer.timeRemaining())
                    .isEqualTo(new TimeRemaining(4, 0, 100));
        }

        @Test
        void startedTimerIsRunning() {
            EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimer();

            ensembleTimer.startTimerAt(Instant.now());

            assertThat(ensembleTimer.state())
                    .isEqualByComparingTo(EnsembleTimer.TimerState.RUNNING);
            assertThat(ensembleTimer.timeRemaining())
                    .isEqualTo(new TimeRemaining(4, 0, 100));
        }

        @Test
        void timeRemainingIsHalfWhenLastTickIsHalfOfDuration() {
            EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimerWith4MinuteDuration();
            Instant timerStartedAt = Instant.now();
            ensembleTimer.startTimerAt(timerStartedAt);

            Instant halfway = timerStartedAt.plus(Duration.ofMinutes(2));
            ensembleTimer.tick(halfway);

            assertThat(ensembleTimer.timeRemaining())
                    .isEqualTo(new TimeRemaining(2, 0, 50));
        }

        @Test
        void timerRemainsRunningWhenTickTimeBeforeEndTime() {
            EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimerWith4MinuteDuration();
            Instant timerStartedAt = Instant.now();
            ensembleTimer.startTimerAt(timerStartedAt);

            Instant oneMilliBeforeEnd = timerStartedAt.plus(Duration.ofMinutes(4).minusMillis(1));
            ensembleTimer.tick(oneMilliBeforeEnd);

            assertThat(ensembleTimer.state())
                    .isEqualByComparingTo(EnsembleTimer.TimerState.RUNNING);
            assertThat(ensembleTimer.timeRemaining().minutes())
                    .isZero();
            assertThat(ensembleTimer.timeRemaining().seconds())
                    .isZero();
            assertThat(ensembleTimer.timeRemaining().percent())
                    .isCloseTo(0, Offset.offset(.001));
        }

        @Test
        void isFinishedWhenTickTimeAtEndTime() {
            EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimerWith4MinuteDuration();
            Instant timerStartedAt = Instant.now();
            ensembleTimer.startTimerAt(timerStartedAt);

            Instant timerFinishedAt = timerStartedAt.plus(Duration.ofMinutes(4));
            ensembleTimer.tick(timerFinishedAt);

            assertThat(ensembleTimer.state())
                    .isEqualByComparingTo(EnsembleTimer.TimerState.FINISHED);
            assertThat(ensembleTimer.timeRemaining())
                    .isEqualTo(new TimeRemaining(0, 0, 0));
        }

        @Test
        void isFinishedWhenTickTimeAfterEndTime() {
            EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimerWith4MinuteDuration();
            Instant timerStartedAt = Instant.now();
            ensembleTimer.startTimerAt(timerStartedAt);

            Instant oneMilliAfterEnd = timerStartedAt.plus(Duration.ofMinutes(4).plusMillis(1));
            ensembleTimer.tick(oneMilliAfterEnd);

            assertThat(ensembleTimer.state())
                    .isEqualByComparingTo(EnsembleTimer.TimerState.FINISHED);
            assertThat(ensembleTimer.timeRemaining())
                    .isEqualTo(new TimeRemaining(0, 0, 0));
        }

    }

    @Nested
    class UnhappyScenarios {

        @Test
        void startTimerThrowsExceptionIfAlreadyRunning() {
            EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimer();
            ensembleTimer.startTimerAt(Instant.now());

            assertThatIllegalStateException()
                    .isThrownBy(() -> ensembleTimer.startTimerAt(Instant.now()))
                    .withMessage("Can't Start Timer when Running");
        }

        @Test
        void timerTickWhenWaitingToStartThrowsException() {
            EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimer();

            Instant tickAtNow = Instant.now();
            assertThatIllegalStateException()
                    .isThrownBy(() -> ensembleTimer.tick(tickAtNow))
                    .withMessage("Timer is Waiting to Start, but Tick was received at %s."
                                         .formatted(tickAtNow));
        }

        @Test
        void tickWhenFinishedThrowsException() {
            EnsembleTimerFactory.Fixture fixture = EnsembleTimerFactory.create4MinuteTimerInFinishedState();

            Instant finishedAt = fixture.timerStartedAt().plus(Duration.ofMinutes(4));
            Instant finishedAtPlus20Millis = finishedAt.plusMillis(20);
            assertThatIllegalStateException()
                    .isThrownBy(() -> fixture.ensembleTimer().tick(finishedAtPlus20Millis))
                    .withMessage("Tick received at %s after Timer already Finished at %s."
                                         .formatted(finishedAtPlus20Millis, finishedAt));
        }

    }

    @Nested
    class ParticipantRotation {

        @Test
        void rolesAssignedUponCreation() {
            MemberId driverId = MemberId.of(3L);
            MemberId navigatorId = MemberId.of(7L);
            MemberId nextDriverId = MemberId.of(2L);
            MemberId participantId1 = MemberId.of(1L);
            MemberId participantId2 = MemberId.of(9L);
            EnsembleTimer ensembleTimer = new EnsembleTimer(EnsembleTimerFactory.IRRELEVANT_ENSEMBLE_ID,
                                                            EnsembleTimerFactory.IRRELEVANT_NAME,
                                                            List.of(nextDriverId,
                                                                    driverId,
                                                                    navigatorId,
                                                                    participantId1,
                                                                    participantId2));

            assertThat(ensembleTimer.rotation().driver())
                    .as("Expected rotation.driver() to be " + driverId)
                    .isEqualTo(driverId);
        }

        void rolesDoNotRotateWhenTimerFinishes() {

            // .as("rotate should not happen until we invoke #nextRound() explicitly")

        }

        @Test
        @Disabled("Until Rotation#rotate is implemented")
        void rolesRotateWhenNextRoundInvokedOnFinishedTimer() {
            MemberId driverId = MemberId.of(3L);
            MemberId navigatorId = MemberId.of(7L);
            MemberId nextDriverId = MemberId.of(2L);
            MemberId participantId1 = MemberId.of(1L);
            MemberId participantId2 = MemberId.of(9L);
            EnsembleTimer ensembleTimer = new EnsembleTimer(
                    EnsembleTimerFactory.IRRELEVANT_ENSEMBLE_ID,
                    EnsembleTimerFactory.IRRELEVANT_NAME,
                    List.of(driverId, navigatorId, nextDriverId, participantId1, participantId2),
                    Duration.ofMinutes(4));
            pushTimerToFinishedState(ensembleTimer);

            ensembleTimer.rotateRoles();

            assertThat(ensembleTimer.rotation().driver())
                    .isEqualTo(nextDriverId);
        }

        private void pushTimerToFinishedState(EnsembleTimer ensembleTimer) {
            Instant timerStartedAt = Instant.now();
            ensembleTimer.startTimerAt(timerStartedAt);
            Instant timerFinishedAt = timerStartedAt.plus(Duration.ofMinutes(4));
            ensembleTimer.tick(timerFinishedAt);
        }
    }
}