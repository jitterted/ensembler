package com.jitterted.mobreg.domain;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class EnsembleTimerTest {

    @Nested
    class TimerState {
        @Test
        void isWaitingToStartHasFullTimeRemainingForNewTimer() {
            EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimer();

            assertThat(ensembleTimer.state())
                    .isEqualByComparingTo(CountdownTimer.TimerState.WAITING_TO_START);
            assertThat(ensembleTimer.timeRemaining())
                    .isEqualTo(new TimeRemaining(4, 0, 100));
        }

        @Test
        void isRunningWhenTimerStarted() {
            EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimer();

            ensembleTimer.startTimerAt(Instant.now());

            assertThat(ensembleTimer.state())
                    .isEqualByComparingTo(CountdownTimer.TimerState.RUNNING);
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
                    .isEqualByComparingTo(CountdownTimer.TimerState.RUNNING);
            assertThat(ensembleTimer.timeRemaining().minutes())
                    .isZero();
            assertThat(ensembleTimer.timeRemaining().seconds())
                    .isZero();
            assertThat(ensembleTimer.timeRemaining().percent())
                    .isCloseTo(0, Offset.offset(.001));
        }

        @Test
        void isPausedWhenTimerPaused() {
            EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimerWith4MinuteDuration();
            Instant timerStartedAt = Instant.now();
            ensembleTimer.startTimerAt(timerStartedAt);
            ensembleTimer.tick(timerStartedAt.plusSeconds(30));

            ensembleTimer.pause();
            ensembleTimer.tick(timerStartedAt.plusSeconds(45));

            assertThat(ensembleTimer.state())
                    .isEqualByComparingTo(CountdownTimer.TimerState.PAUSED);
            assertThat(ensembleTimer.timeRemaining())
                    .isEqualTo(TimeRemaining.from(Duration.ofMinutes(3).plusSeconds(30),
                                                  Duration.ofMinutes(4)));
        }

        @Test
        void isResumedWhenTimerResumed() {
            EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimer();
            ensembleTimer.startTimerAt(Instant.now());
            ensembleTimer.pause();

            ensembleTimer.resume();

            assertThat(ensembleTimer.state())
                    .isEqualByComparingTo(CountdownTimer.TimerState.RUNNING);
        }

        @Test
        void isFinishedWhenTickTimeAtEndTime() {
            EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimerWith4MinuteDuration();
            Instant timerStartedAt = Instant.now();
            ensembleTimer.startTimerAt(timerStartedAt);

            Instant timerFinishedAt = timerStartedAt.plus(Duration.ofMinutes(4));
            ensembleTimer.tick(timerFinishedAt);

            assertThat(ensembleTimer.state())
                    .isEqualByComparingTo(CountdownTimer.TimerState.FINISHED);
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

            assertThat(ensembleTimer.timeRemaining())
                    .isEqualTo(new TimeRemaining(0, 0, 0));
            assertThat(ensembleTimer.state())
                    .isEqualByComparingTo(CountdownTimer.TimerState.FINISHED);
        }

        @Test
        public void isWaitingToStartWhenRotateFinishedTimer() {
            EnsembleTimerFactory.Fixture fixture = EnsembleTimerFactory.create4MinuteTimerInFinishedState();

            fixture.ensembleTimer().rotateRoles();

            assertThat(fixture.ensembleTimer().state())
                    .isEqualByComparingTo(CountdownTimer.TimerState.WAITING_TO_START);
            assertThat(fixture.ensembleTimer().timeRemaining())
                    .isEqualTo(new TimeRemaining(4, 0, 100));
        }

    }

    @Nested
    class UnhappyScenarios {

        @Test
        public void rotateTimerWhenTimerNotStartedThrowsException() {
            EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimer();

            assertThatIllegalStateException()
                    .isThrownBy(ensembleTimer::rotateRoles)
                    .withMessage("Can't Rotate when timer state is WAITING_TO_START");
        }

        @Test
        public void rotateTimerWhenTimerRunningThrowsException() {
            EnsembleTimer ensembleTimer = EnsembleTimerFactory.createTimerWith4MinuteDuration();
            ensembleTimer.startTimerAt(Instant.now());

            assertThatIllegalStateException()
                    .isThrownBy(ensembleTimer::rotateRoles)
                    .withMessage("Can't Rotate when timer state is RUNNING");
        }

    }

    @Nested
    class ParticipantRotation {

        @Test
        void rolesAssignedUponCreation() {
            RotationFixture fixture = createParticipantRotation();

            assertThat(fixture.ensembleTimer().rotation().driver())
                    .as("Expected rotation.driver() to be " + fixture.driver())
                    .isEqualTo(fixture.driver());
        }

        @Test
        void rolesDoNotRotateWhenTimerFinishes() {
            RotationFixture fixture = createParticipantRotation();

            EnsembleTimerFactory.pushTimerToFinishedState(fixture.ensembleTimer());

            assertThat(fixture.ensembleTimer().rotation().driver())
                    .as("rotate should not happen until we invoke #nextRound() explicitly")
                    .isEqualTo(fixture.driver());
        }

        @Test
        void rolesRotateWhenNextRoundInvokedOnFinishedTimer() {
            RotationFixture fixture = createParticipantRotation();
            EnsembleTimerFactory.pushTimerToFinishedState(fixture.ensembleTimer());

            fixture.ensembleTimer().rotateRoles();

            assertThat(fixture.ensembleTimer().rotation().driver())
                    .isEqualTo(fixture.nextDriver());
        }

        private RotationFixture createParticipantRotation() {
            Member nextDriver = MemberFactory.createMember(1, "One", "irrelevant");
            Member driver = MemberFactory.createMember(2, "Two", "irrelevant");
            Member navigator = MemberFactory.createMember(3, "Three", "irrelevant");
            Member participant1 = MemberFactory.createMember(4, "Four", "irrelevant");
            Member participant2 = MemberFactory.createMember(5, "Five", "irrelevant");

            List<Member> allParticipants = List.of(nextDriver,
                                                   driver,
                                                   navigator,
                                                   participant1,
                                                   participant2);

            EnsembleTimer ensembleTimer = new EnsembleTimer(
                    EnsembleTimerFactory.IRRELEVANT_ENSEMBLE_ID,
                    EnsembleTimerFactory.IRRELEVANT_NAME,
                    allParticipants,
                    Duration.ofMinutes(4));
            return new RotationFixture(driver, nextDriver, ensembleTimer);
        }

        private record RotationFixture(Member driver, Member nextDriver,
                                       EnsembleTimer ensembleTimer) {
        }

    }
}