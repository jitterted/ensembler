package com.jitterted.moborg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final GrantedAuthoritiesMapper userAuthoritiesMapper;

    @Autowired
    public WebSecurityConfig(GrantedAuthoritiesMapper userAuthoritiesMapper) {
        this.userAuthoritiesMapper = userAuthoritiesMapper;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .authorizeRequests()
                .mvcMatchers("/", "/error")
                    .permitAll()
                .mvcMatchers("/admin/**")
                    .hasAuthority("ROLE_ADMIN")
                .mvcMatchers("/member/**")
                    .hasAuthority("ROLE_MEMBER")
                .and()
            .logout(l -> l
                    .logoutSuccessUrl("/").permitAll()
            )
            .oauth2Login()
                .userInfoEndpoint()
                    .userAuthoritiesMapper(userAuthoritiesMapper);
        // @formatter:on
    }

}

