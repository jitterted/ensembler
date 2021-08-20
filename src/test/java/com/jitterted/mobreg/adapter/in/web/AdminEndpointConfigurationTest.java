package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.HuddleService;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.OAuth2UserFactory;
import com.jitterted.mobreg.domain.port.HuddleRepository;
import com.jitterted.mobreg.domain.port.MemberRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO: create test configuration that uses Fake repositories
@WebMvcTest
@Tag("mvc")
// TODO: roles aren't needed here anymore
@WithMockUser(username = "username", authorities = {"ROLE_MEMBER","ROLE_ADMIN"})
public class AdminEndpointConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    HuddleService huddleService;

    @MockBean
    HuddleRepository huddleRepository;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Test
    public void getOfDashboardEndpointReturns200Ok() throws Exception {
        Member member = new Member("Ted", "tedyoung", "ROLE_MEMBER", "ROLE_ADMIN");
        member.setId(MemberId.of(1L));
        when(memberRepository.findByGithubUsername("tedyoung")).thenReturn(Optional.of(member));
        mockMvc.perform(get("/admin/dashboard")
                                .with(OAuth2UserFactory.oAuth2User("ROLE_ADMIN")))
               .andExpect(status().isOk());
    }

    @Test
    public void getOfHuddleDetailEndpointReturns200Ok() throws Exception {
        createStubServiceReturningHuddleWithIdOf(13L);

        mockMvc.perform(get("/admin/huddle/13"))
               .andExpect(status().isOk());
    }

    @Test
    public void postToScheduleHuddleEndpointRedirects() throws Exception {
        mockMvc.perform(post("/admin/schedule")
                                .param("name", "test")
                                .param("zoomMeetingLink", "https://zoom.us/j/test?pwd=testy")
                                .param("date", "2021-04-30")
                                .param("time", "09:00")
                                .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    public void postToRegisterParticipantEndpointRedirects() throws Exception {
        createStubServiceReturningHuddleWithIdOf(23L);
        mockMvc.perform(post("/admin/register")
                                .param("huddleId", "23")
                                .param("name", "participant")
                                .param("githubUsername", "mygithub")
                                .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

    private void createStubServiceReturningHuddleWithIdOf(long id) {
        Huddle dummyHuddle = new Huddle("dummy", ZonedDateTime.now());
        HuddleId huddleId = HuddleId.of(id);
        dummyHuddle.setId(huddleId);
        when(huddleService.findById(huddleId))
                .thenReturn(Optional.of(dummyHuddle));
    }

}
