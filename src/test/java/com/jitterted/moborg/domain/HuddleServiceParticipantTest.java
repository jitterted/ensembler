package com.jitterted.moborg.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleServiceParticipantTest {

    @Test
    public void givenParticipantRegistersForHuddleThenWillBeFoundInHuddle() throws Exception {
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = new HuddleService(huddleRepository);
        Huddle huddle = new Huddle("test", ZonedDateTime.now());
        HuddleId huddleId = huddleRepository.save(huddle).getId();

        huddleService.registerParticipant(huddleId, "Participant J. Name", "pjname");

        assertThat(huddle.participants())
                .extracting(Participant::githubUsername)
                .containsOnly("pjname");
    }
}