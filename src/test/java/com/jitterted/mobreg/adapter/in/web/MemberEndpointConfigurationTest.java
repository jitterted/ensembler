package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.HuddleService;
import com.jitterted.mobreg.domain.OAuth2UserFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.OAuth2LoginRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Tag("mvc")
@WithMockUser(username = "username", authorities = {"ROLE_MEMBER"})
public class MemberEndpointConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    HuddleService huddleService;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Test
    public void getOfMemberRegisterEndpointReturns200Ok() throws Exception {
        mockMvc.perform(get("/member/register")
                                .with(oAuth2User()))
               .andExpect(status().isOk());
    }

    @Test
    public void postToRegisterRedirects() throws Exception {
        mockMvc.perform(post("/member/register")
                                .param("huddleId", "1")
                                .param("memberId", "2")
                                .with(oAuth2User())
                                .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

    @NotNull
    private OAuth2LoginRequestPostProcessor oAuth2User() {
        return SecurityMockMvcRequestPostProcessors
                .oauth2Login()
                .oauth2User(
                        OAuth2UserFactory.createOAuth2UserWithMemberRole("tedyoung")
                );
    }

}
