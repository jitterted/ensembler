package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.in.web.OAuth2UserFactory;
import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled // figure out what's wrong
@WebMvcTest({MemberController.class, MemberProfileController.class})
@Tag("mvc")
@WithMockUser(username = "username", authorities = {"ROLE_MEMBER"})
class MemberEndpointConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    EnsembleService ensembleService;

    @MockBean
    EnsembleRepository ensembleRepository;

    @MockBean
    MemberService memberService;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Test
    void getOfMemberRegisterEndpointReturns200Ok() throws Exception {
        Member member = new Member("Ted", "tedyoung", "ROLE_MEMBER");
        member.setId(MemberId.of(1L));
        when(memberService.findByGithubUsername("tedyoung")).thenReturn(member);
        mockMvc.perform(get("/member/register")
                                // TODO: roles aren't needed here anymore
                                .with(OAuth2UserFactory.oAuth2User("ROLE_MEMBER")))
               .andExpect(status().isOk());
    }

    @Test
    void postToRegisterRedirects() throws Exception {
        mockMvc.perform(post("/member/accept")
                                .param("ensembleId", "1")
                                .param("memberId", "1")
                                // TODO: roles aren't needed here anymore
                                .with(OAuth2UserFactory.oAuth2User("ROLE_MEMBER"))
                                .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    void postToDeclineRedirects() throws Exception {
        mockMvc.perform(post("/member/decline")
                                .param("ensembleId", "1")
                                .param("memberId", "1")
                                .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    void getOfMemberProfileEndpointReturns200Ok() throws Exception {
        Member member = new Member("Ted", "tedyoung", "ROLE_MEMBER");
        member.setId(MemberId.of(1L));
        when(memberService.findByGithubUsername("tedyoung")).thenReturn(member);
        mockMvc.perform(get("/member/profile")
                                .with(OAuth2UserFactory.oAuth2User("ROLE_MEMBER")))
               .andExpect(status().isOk());
    }

    @Test
    void postToMemberProfileEndpointRedirects() throws Exception {
        mockMvc.perform(post("/member/profile")
                                .param("firstName", "1st")
                                .param("githubUsername", "abc")
                                .param("email", "me@example.co")
                                // TODO: roles aren't needed here anymore
                                .with(OAuth2UserFactory.oAuth2User("ROLE_MEMBER"))
                                .with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

}
