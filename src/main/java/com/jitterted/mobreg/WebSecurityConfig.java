package com.jitterted.mobreg;

import com.jitterted.mobreg.adapter.in.web.member.MemberDeniedRedirectToUserOnboardingHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final GrantedAuthoritiesMapper userAuthoritiesMapper;

    public WebSecurityConfig(GrantedAuthoritiesMapper userAuthoritiesMapper) {
        this.userAuthoritiesMapper = userAuthoritiesMapper;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/**", "/error")
                        .permitAll()
                        .requestMatchers("/user/**", "/invite")
                        .hasAuthority("ROLE_USER")
                        .requestMatchers("/admin/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/member/**")
                        .hasAuthority("ROLE_MEMBER"))
                .exceptionHandling(handling -> handling
                        .accessDeniedHandler(new MemberDeniedRedirectToUserOnboardingHandler()))
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true))
                .oauth2Login(login -> login
                        .userInfoEndpoint(endpoint -> endpoint
                                .userAuthoritiesMapper(userAuthoritiesMapper)));
        return http.build();
        // @formatter:on
    }

}

