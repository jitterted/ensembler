package com.jitterted.moborg.adapter.out.mobtimer;

import com.jitterted.moborg.domain.Huddle;
import com.jitterted.moborg.domain.Participant;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class MobParticipantsDtoCreationTest {

    @Test
    public void convertsHuddleToMobParticipantsDto() throws Exception {
        Huddle huddle = new Huddle("test", ZonedDateTime.now());
        huddle.register(new Participant("Participant", "", "", "", false));

        MobParticipantsDto dto = MobParticipantsDto.from(huddle);

        assertThat(dto.getMob())
                .extracting(PersonDto::getName)
                .containsOnly("Participant");
    }
}