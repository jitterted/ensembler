package com.jitterted.mobreg.adapter.out.mobtimer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZonedDateTime;

@Disabled
@SpringBootTest
@Tag("manual") // excluded from test run configuration
class MobTimerMessageSenderTest {

    @Autowired
    MobTimerMessageSender mobTimerMessageSender;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberService memberService;

    @Test
    public void mobPeopleUpdated() throws Exception {
        Huddle huddle = new Huddle("test", ZonedDateTime.now());
        Member member = new Member("WooHoo", "");
        Member savedMember = memberService.save(member);
        huddle.registerById(savedMember.getId());

        mobTimerMessageSender.updateParticipantsTo(huddle);
    }

}