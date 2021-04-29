package com.jitterted.moborg.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

public class HuddleParticipantsTest {

  @Test
  public void newHuddleHasZeroParticipants() throws Exception {
    Huddle huddle = new Huddle("test", ZonedDateTime.now());

    assertThat(huddle.numberRegistered())
        .isZero();
    assertThat(huddle.participants())
        .isEmpty();
  }

  @Test
  public void addOneParticipantToHuddleRemembersTheParticipant() throws Exception {
    Huddle huddle = new Huddle("huddle", ZonedDateTime.now());

    Participant participant = new Participant("name", "github", "email", "discord", false);
    huddle.register(participant);

    assertThat(huddle.numberRegistered())
        .isEqualTo(1);
    assertThat(huddle.participants())
        .containsOnly(participant);
  }
}
