package com.jitterted.mobreg.adapter.in.web;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jitterted.mobreg.domain.HuddleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Disabled
@WebMvcTest
@WithMockUser(username = "username", authorities = {"ROLE_MEMBER"})
public class MemberEndpointHtmlTest {

    @Autowired
    WebClient webClient;

    @MockBean
    HuddleService huddleService;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @BeforeEach
    void setup(WebApplicationContext context) {
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        webClient = MockMvcWebClientBuilder
                .mockMvcSetup(mockMvc)
                .webAppContextSetup(context, springSecurity())
                .build();
    }

    @Test
    public void html() throws Exception {
        HtmlPage memberRegisterPage = webClient
                .getPage("http://localhost/member/register");

        System.out.println(memberRegisterPage.getForms().get(0).getElementsByTagName("input"));

    }

}
