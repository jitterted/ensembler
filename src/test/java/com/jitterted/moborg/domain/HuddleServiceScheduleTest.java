package com.jitterted.moborg.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleServiceScheduleTest {

  @Test
  public void scheduledHuddleIsReturnedForAllHuddles() throws Exception {
    HuddleService huddleService = new HuddleService(new InMemoryHuddleRepository());

    huddleService.scheduleHuddle("Name", ZonedDateTime.now());

    assertThat(huddleService.allHuddles())
        .hasSize(1);
  }

}