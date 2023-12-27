package com.jitterted.mobreg.adapter.out.mobtimer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;
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
    EnsembleService ensembleService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EnsembleRepository ensembleRepository;

    @Test
    void mobPeopleUpdated() throws Exception {
        Ensemble ensemble = new Ensemble("test", ZonedDateTime.now());
        Member member = new Member("Jane", "janeuser");
        Member savedMember = memberService.save(member);
        ensemble.joinAsParticipant(savedMember.getId());
        member = new Member("Jack", "jackuser");
        savedMember = memberService.save(member);
        ensemble.joinAsParticipant(savedMember.getId());

        mobTimerMessageSender.updateParticipantsTo(ensemble);
    }

}