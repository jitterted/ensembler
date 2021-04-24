package com.jitterted.moborg.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleServiceScheduleTest {

  @Test
  public void singleScheduledHuddleIsReturnedForAllHuddles() throws Exception {
    HuddleService huddleService = new HuddleService(new InMemoryHuddleRepository());

    huddleService.scheduleHuddle("Name", ZonedDateTime.now());

    assertThat(huddleService.allHuddles())
        .hasSize(1);
  }

}