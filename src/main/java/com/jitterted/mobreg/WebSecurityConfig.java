package com.jitterted.mobreg;

import com.jitterted.mobreg.adapter.in.web.member.MemberDeniedRedirectToUserOnboardingHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    private final GrantedAuthoritiesMapper userAuthoritiesMapper;
//
//    @Autowired
//    public WebSecurityConfig(GrantedAuthoritiesMapper userAuthoritiesMapper) {
//        this.userAuthoritiesMapper = userAuthoritiesMapper;
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .authorizeRequests()
                .mvcMatchers("/", "/error")
                    .permitAll()
                .mvcMatchers("/user/**", "/invite")
                    .hasAuthority("ROLE_USER")
                .mvcMatchers("/admin/**")
                    .hasAuthority("ROLE_ADMIN")
                .mvcMatchers("/member/**")
                    .hasAuthority("ROLE_MEMBER")
                .and()
            .exceptionHandling()
                .accessDeniedHandler(new MemberDeniedRedirectToUserOnboardingHandler())
                .and()
            .logout()
                .logoutSuccessUrl("/")
                    .permitAll()
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .and()
            .oauth2Login(Customizer.withDefaults());
        // @formatter:on
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority oidcUserAuthority) {

                    OidcIdToken idToken = oidcUserAuthority.getIdToken();
                    OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();

                    ((List<String>) idToken.getClaims().get("roles")).stream()
                                                                     .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                                                                     .forEach(mappedAuthorities::add);

                } else if (authority instanceof OAuth2UserAuthority oauth2UserAuthority) {

                    Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();

                    // Map the attributes found in userAttributes
                    // to one or more GrantedAuthority's and add it to mappedAuthorities

                }
            });

            return mappedAuthorities;
        };
    }

}

