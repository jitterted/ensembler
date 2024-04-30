package com.jitterted.mobreg.adapter.out.clock;

import com.jitterted.mobreg.application.EnsembleTimerTickHandler;
import com.jitterted.mobreg.domain.EnsembleId;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.*;

class ScheduledExecutorSecondsTickerTest {

    @Tag("manual")
    @Test
    void startedTickerTicksOncePerSecond() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);

        EnsembleTimerTickHandler ensembleTimerTickHandler = (ensembleId, now) -> {
            System.out.printf("Countdown at %s, time is %s%n", countDownLatch.getCount(), now);
            countDownLatch.countDown();
        };

        ScheduledExecutorSecondsTicker ticker = new ScheduledExecutorSecondsTicker();

        System.out.printf("Before start, time is %s%n", Instant.now());

        long startedAt = System.currentTimeMillis();

        ticker.start(EnsembleId.of(1), ensembleTimerTickHandler);

        countDownLatch.await();

        assertThat(System.currentTimeMillis() - startedAt)
                .isGreaterThan(1000);
    }

    @Test
    void exceptionThrownWhenStartInvokedButCountdownAlreadyScheduled() {
        ScheduledExecutorSecondsTicker ticker = new ScheduledExecutorSecondsTicker();
        ticker.start(EnsembleId.of(3), (ensembleId, now) -> {});

        assertThatIllegalStateException()
                .isThrownBy(() -> ticker.start(EnsembleId.of(2), (ensembleId, now) -> {}))
                .withMessage("Countdown timer already scheduled for EnsembleId=3");
    }

    @Test
    void countdownStartedWhenPreviousCountdownStopped() {
        ScheduledExecutorSecondsTicker ticker = new ScheduledExecutorSecondsTicker();
        ticker.start(EnsembleId.of(4), (ensembleId, now) -> {});
        ticker.stop();

        assertThatCode(() -> ticker.start(EnsembleId.of(5), (ensembleId, now) -> {}))
                .doesNotThrowAnyException();
    }
}