package com.jitterted.mobreg.adapter.out.mobtimer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.Participant;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZonedDateTime;

@SpringBootTest
@Tag("manual") // excluded from test run configuration
class MobTimerMessageSenderTest {

    @Autowired
    MobTimerMessageSender mobTimerMessageSender;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void mobPeopleUpdated() throws Exception {
        Huddle huddle = new Huddle("test", ZonedDateTime.now());
        huddle.register(new Participant("WooHoo", "", "", "", false));

        mobTimerMessageSender.updateParticipantsTo(huddle);
    }

}