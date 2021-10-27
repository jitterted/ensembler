package com.jitterted.mobreg.adapter.out.mobtimer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleService;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberService;
import com.jitterted.mobreg.domain.port.HuddleRepository;
import com.jitterted.mobreg.domain.port.MemberRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;

@SpringBootTest
@WithMockUser(username = "username", authorities = {"ROLE_MEMBER", "ROLE_ADMIN"})
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Tag("manual") // excluded from test run configuration
@Disabled // so it doesn't run when Maven runs tests
class MobTimerMessageSenderTest {

    @Autowired
    MobTimerMessageSender mobTimerMessageSender;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberService memberService;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Autowired
    HuddleService huddleService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    HuddleRepository huddleRepository;

    @Test
    public void mobPeopleUpdated() throws Exception {
        Huddle huddle = new Huddle("test", ZonedDateTime.now());
        Member member = new Member("Jane", "janeuser");
        Member savedMember = memberService.save(member);
        huddle.registerById(savedMember.getId());
        member = new Member("Jack", "jackuser");
        savedMember = memberService.save(member);
        huddle.registerById(savedMember.getId());

        mobTimerMessageSender.updateParticipantsTo(huddle);
    }

}