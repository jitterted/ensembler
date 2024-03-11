package com.jitterted.mobreg.adapter.in.web.member;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.port.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.web.method.annotation.CsrfTokenArgumentResolver;
import org.springframework.security.web.method.annotation.CurrentSecurityContextArgumentResolver;
import org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

import java.util.List;

@Disabled("Waiting for HtmlUnit to be updated from Spring")
@WebMvcTest(controllers = MemberController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class)})
@WithMockUser(username = "username", authorities = {"ROLE_MEMBER"})
@Import(MyWebMvcSecurityConfiguration.class)
class MemberEndpointHtmlTest {

    //    @Autowired
    WebClient webClient;

    @MockBean
    MemberService memberService;

    @MockBean
    EnsembleService ensembleService;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @MockBean
    MemberRepository memberRepository;

    @BeforeEach
    void setup(WebApplicationContext context) {
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        MockMvcWebClientBuilder
                .mockMvcSetup(mockMvc);
        webClient = MockMvcWebClientBuilder
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void html() throws Exception {
        HtmlPage memberRegisterPage = webClient
                .getPage("http://localhost/member/register");

        System.out.println(memberRegisterPage.getForms().get(0).getElementsByTagName("input"));

    }

}

class MyWebMvcSecurityConfiguration implements WebMvcConfigurer, ApplicationContextAware {

    private BeanResolver beanResolver;

    @Override
    @SuppressWarnings("deprecation")
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        AuthenticationPrincipalArgumentResolver authenticationPrincipalResolver = new AuthenticationPrincipalArgumentResolver();
        authenticationPrincipalResolver.setBeanResolver(this.beanResolver);
        argumentResolvers.add(authenticationPrincipalResolver);
        argumentResolvers
                .add(new org.springframework.security.web.bind.support.AuthenticationPrincipalArgumentResolver());
        CurrentSecurityContextArgumentResolver currentSecurityContextArgumentResolver = new CurrentSecurityContextArgumentResolver();
        currentSecurityContextArgumentResolver.setBeanResolver(this.beanResolver);
        argumentResolvers.add(currentSecurityContextArgumentResolver);
        argumentResolvers.add(new CsrfTokenArgumentResolver());
    }

    @Bean
    RequestDataValueProcessor requestDataValueProcessor() {
        return new CsrfRequestDataValueProcessor();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanResolver = new BeanFactoryResolver(applicationContext.getAutowireCapableBeanFactory());
    }

}
