package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.in.web.OAuth2UserFactory;
import com.jitterted.mobreg.adapter.in.web.member.InviteProcessController;
import com.jitterted.mobreg.adapter.out.jdbc.InviteJdbcRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest({InviteProcessController.class})
@Tag("mvc")
public class InviteEditorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    InviteJdbcRepository inviteJdbcRepository;

    @MockBean
    MemberRepository memberRepository;

    @Test
    void getToInviteIdEndpointIs200Ok() throws Exception {
        mockMvc.perform(get("/invite?invite_id=testing123")
                                .with(OAuth2UserFactory.oAuth2User("ROLE_USER")))
               .andExpect(status().isOk())
               .andExpect(view().name("invite-invalid"));
    }

}