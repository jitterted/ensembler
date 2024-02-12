package com.jitterted.mobreg.adapter.out.clock;

import com.jitterted.mobreg.domain.EnsembleId;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.*;

@Tag("manual")
class ScheduledExecutorSecondsTickerTest {

    @Test
    void startedTickerTicksOncePerSecond() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);

        ScheduledExecutorSecondsTicker ticker = new ScheduledExecutorSecondsTicker(
                (ensembleId, now) -> {
                    System.out.printf("Countdown at %s, time is %s%n", countDownLatch.getCount(), now);
                    countDownLatch.countDown();
                });

        System.out.printf("Before start, time is %s%n", Instant.now());

        long startedAt = System.currentTimeMillis();

        ticker.start(EnsembleId.of(1));

        countDownLatch.await();

        assertThat(System.currentTimeMillis() - startedAt)
                .isGreaterThan(1000);
    }
}