package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.HuddleService;
import com.jitterted.mobreg.application.MemberFactory;
import com.jitterted.mobreg.application.port.HuddleRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.OAuth2UserFactory;
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
@WithMockUser(username = "username", authorities = {"ROLE_MEMBER", "ROLE_ADMIN"})
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
        createStubMemberRepositoryWithMember(1L, "Ted", "tedyoung", "ROLE_MEMBER", "ROLE_ADMIN");
        mockMvc.perform(get("/admin/dashboard")
                                .with(OAuth2UserFactory.oAuth2User("ROLE_ADMIN")))
               .andExpect(status().isOk());
    }

    @Test
    public void getOfHuddleDetailEndpointReturns200Ok() throws Exception {
        createStubHuddleServiceReturningHuddleWithIdOf(13L);

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
                                .param("timezone", "America/Los_Angeles")
                                .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    public void postToChangeHuddleEndpointRedirects() throws Exception {
        mockMvc.perform(post("/admin/huddle/17")
                                .param("name", "New Name")
                                .param("date", "2021-11-30")
                                .param("time", "10:00")
                                .param("timezone", "America/Los_Angeles")
                                .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    public void postToRegisterParticipantEndpointRedirects() throws Exception {
        createStubHuddleServiceReturningHuddleWithIdOf(23L);
        createStubMemberRepositoryWithMember(1L, "participant", "mygithub", new String[]{"ROLE_USER", "ROLE_MEMBER"});
        mockMvc.perform(post("/admin/register")
                                .param("huddleId", "23")
                                .param("name", "participant")
                                .param("githubUsername", "mygithub")
                                .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

    private void createStubMemberRepositoryWithMember(long id, String firstName, String githubUsername, String... roles) {
        Member member = MemberFactory.createMember(id, firstName, githubUsername, roles);
        when(memberRepository.findByGithubUsername(githubUsername))
                .thenReturn(Optional.of(member));
    }

    @Test
    public void postToCompleteEndpointRedirects() throws Exception {
        createStubHuddleServiceReturningHuddleWithIdOf(13);
        mockMvc.perform(post("/admin/huddle/13/complete")
                                .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }


    private void createStubHuddleServiceReturningHuddleWithIdOf(long id) {
        Huddle dummyHuddle = new Huddle("dummy", ZonedDateTime.now());
        HuddleId huddleId = HuddleId.of(id);
        dummyHuddle.setId(huddleId);
        when(huddleService.findById(huddleId))
                .thenReturn(Optional.of(dummyHuddle));
    }

}
