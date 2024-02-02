package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.in.web.TestAdminConfiguration;
import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.time.ZonedDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO: create test configuration that uses Fake repositories
@WebMvcTest({AdminDashboardController.class, MemberManagementController.class})
@Import(TestAdminConfiguration.class)
@Tag("mvc")
// TODO: roles aren't needed here anymore?
@WithMockUser(username = "username", authorities = {"ROLE_MEMBER", "ROLE_ADMIN"})
class AdminEndpointConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    EnsembleService ensembleService;

    @Autowired
    EnsembleRepository ensembleRepository;

    @Autowired
    MemberRepository memberRepository;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Disabled // need to figure out what happened to oAuth
    @Test
    void getOfDashboardEndpointReturns200Ok() throws Exception {
        createStubMemberRepositoryWithMember(1L, "Ted", "tedyoung", "ROLE_MEMBER", "ROLE_ADMIN");
        mockMvc.perform(get("/admin/dashboard")
//                                .with(OAuth2UserFactory.oAuth2User("ROLE_ADMIN"))
               )
               .andExpect(status().isOk());
    }

    @Test
    void getOfEnsembleDetailEndpointReturns200Ok() throws Exception {
        createStubEnsembleServiceReturningEnsembleWithIdOf(13L);

        mockMvc.perform(get("/admin/ensemble/13"))
               .andExpect(status().isOk());
    }

    @Test
    void postToScheduleEnsembleEndpointRedirects() throws Exception {
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
    void postToChangeEnsembleEndpointRedirects() throws Exception {
        createStubEnsembleServiceReturningEnsembleWithIdOf(17L);
        mockMvc.perform(post("/admin/ensemble/17")
                                .param("name", "New Name")
                                .param("zoomMeetingLink", "https://updated.link")
                                .param("date", "2021-11-30")
                                .param("time", "10:00")
                                .param("timezone", "America/Los_Angeles")
                                .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    void postToRegisterParticipantEndpointRedirects() throws Exception {
        createStubEnsembleServiceReturningEnsembleWithIdOf(23);
        createStubMemberRepositoryWithMember(1L, "participant", "mygithub", "ROLE_USER", "ROLE_MEMBER");
        mockMvc.perform(post("/admin/register")
                                .param("ensembleId", "23")
                                .param("name", "participant")
                                .param("githubUsername", "mygithub")
                                .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    void postToCompleteEndpointRedirects() throws Exception {
        createStubEnsembleServiceReturningEnsembleWithIdOf(13);
        mockMvc.perform(post("/admin/ensemble/13/complete")
                                .param("recordingLink", "https://completed.link")
                                .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    void postToCancelEndpointRedirects() throws Exception {
        createStubEnsembleServiceReturningEnsembleWithIdOf(11);
        mockMvc.perform(post("/admin/ensemble/11/cancel")
                                .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

    private void createStubMemberRepositoryWithMember(long id, String firstName, String githubUsername, String... roles) {
        Member member = MemberFactory.createMember(id, firstName, githubUsername, roles);
        memberRepository.save(member);
    }


    private void createStubEnsembleServiceReturningEnsembleWithIdOf(long id) {
        Ensemble dummyEnsemble = new Ensemble("dummy", URI.create("https://dummy.link"), ZonedDateTime.now());
        dummyEnsemble.setId(EnsembleId.of(id));
        ensembleRepository.save(dummyEnsemble);
    }

}
