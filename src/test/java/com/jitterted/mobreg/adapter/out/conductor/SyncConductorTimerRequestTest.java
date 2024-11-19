package com.jitterted.mobreg.adapter.out.conductor;

import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.EnsembleTimerFactory;
import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SyncConductorTimerRequestTest {

    private static final EnsembleId IRRELEVANT_ENSEMBLE_ID = EnsembleId.of(42);
    private static final String IRRELEVANT_ENSEMBLE_NAME = "My Ensemble";

    @Nested
    class TimerState {
        @Test
        void waitingTimerIsMappedToConductorStateWaiting() {
            EnsembleTimer timer = EnsembleTimerFactory.createTimer();

            SyncConductorTimerRequest request = SyncConductorTimerRequest.from(timer);

            assertThat(request.state())
                    .isEqualTo("Waiting");
        }

        @Test
        void runningTimerIsMappedToConductorStateRunning() {
            EnsembleTimer timer = EnsembleTimerFactory.createTimer();
            Instant startedAt = Instant.now();
            timer.startTimerAt(startedAt);
            timer.tick(startedAt.plusSeconds(10));

            SyncConductorTimerRequest request = SyncConductorTimerRequest.from(timer);

            assertThat(request.state())
                    .isEqualTo("Running");
        }

        @Test
        void pausedTimerIsMappedToConductorStatePaused() {
            EnsembleTimer timer = EnsembleTimerFactory.createTimer();
            timer.startTimerAt(Instant.now());
            timer.pause();

            SyncConductorTimerRequest request = SyncConductorTimerRequest.from(timer);

            assertThat(request.state())
                    .isEqualTo("Paused");
        }

        @Test
        void finishedTimerIsMappedToConductorStateFinished() {
            EnsembleTimer timer = EnsembleTimerFactory
                    .create4MinuteTimerInFinishedState()
                    .ensembleTimer();

            SyncConductorTimerRequest request = SyncConductorTimerRequest.from(timer);

            assertThat(request.state())
                    .isEqualTo("Finished");
        }
    }

    @Nested
    class RemainingTime {
        @Test
        void waitingTimerHasFullDurationRemaining() {
            var timer = EnsembleTimerFactory.createTimerWith4MinuteDuration();

            SyncConductorTimerRequest request = SyncConductorTimerRequest.from(timer);

            assertThat(request.timeRemainingSeconds())
                    .isEqualTo(4 * 60);
        }

        @Test
        void runningTimeHasTimeRemainingInSeconds() {
            var timer = EnsembleTimerFactory.createTimerWith4MinuteDuration();
            Instant startedAt = Instant.now();
            timer.startTimerAt(startedAt);
            timer.tick(startedAt.plusSeconds(15));

            SyncConductorTimerRequest request = SyncConductorTimerRequest.from(timer);

            assertThat(request.timeRemainingSeconds())
                    .isEqualTo(4 * 60 - 15);
        }
    }

    @Test
    void rotationIsMappedToRequestParts() {
        EnsembleTimer timer = new EnsembleTimer(
                IRRELEVANT_ENSEMBLE_ID,
                IRRELEVANT_ENSEMBLE_NAME,
                List.of(
                        new Member("Alex", "alex_github"),
                        new Member("Berta", "berta_github"),
                        new Member("Chloe", "chloe_github"),
                        new Member("David", "david_github"),
                        new Member("Erika", "erika_github")
                ));

        SyncConductorTimerRequest request = SyncConductorTimerRequest.from(timer);

        assertThat(request.navigator())
                .isEqualTo("Chloe");
        assertThat(request.driver())
                .isEqualTo("Berta");
        assertThat(request.nextDriver())
                .isEqualTo("Alex");
        assertThat(request.restOfParticipants())
                .containsExactly("David", "Erika");
    }

}