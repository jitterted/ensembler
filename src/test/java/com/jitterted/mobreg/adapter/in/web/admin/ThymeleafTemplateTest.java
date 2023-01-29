package com.jitterted.mobreg.adapter.in.web.admin;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jitterted.mobreg.NoSecurityTestConfiguration;
import com.jitterted.mobreg.WebSecurityConfig;
import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Import(NoSecurityTestConfiguration.class)
@WebMvcTest(
        controllers = AdminDashboardController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = WebSecurityConfig.class)
)
public class ThymeleafTemplateTest {

    @Autowired
//    private MockMvc mockMvc;
    private WebApplicationContext webApplicationContext;

    @MockBean
    EnsembleService ensembleService;

    @MockBean
    MemberService memberService;

    @Test
    @WithMockUser
    void testTemplate() throws Exception {
        WebClient webClient = MockMvcWebClientBuilder
                .webAppContextSetup(webApplicationContext, springSecurity())
//                .mockMvcSetup(mockMvc)
                .build();

        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);

        HtmlPage page = webClient.getPage("http://localhost/admin/dashboard");
        String pageContent = page.asXml();

        assertThat(page.getWebResponse().getStatusCode())
                .isEqualTo(200);

        assertThat(page.getForms().size())
                .isEqualTo(1);

        assertTrue(pageContent.contains("<h1>Hello, Thymeleaf!</h1>"));
    }
}

