package com.jitterted.moborg;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .authorizeRequests()
                .antMatchers("/", "/error")
                    .permitAll()
                .antMatchers("/admin/**")
                    .hasAuthority("ROLE_ADMIN")
                .antMatchers("/member/**")
                    .hasAuthority("ROLE_MEMBER")
                .and()
            .logout(l -> l
                    .logoutSuccessUrl("/").permitAll()
            )
            .oauth2Login()
                .userInfoEndpoint()
                    .userAuthoritiesMapper(userAuthoritiesMapper());
        // @formatter:on
    }

    private GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                if (authority instanceof OAuth2UserAuthority oauth2UserAuthority) {

                    Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();
                    String githubLoginUsername = (String) userAttributes.get("login");
                    if (githubLoginUsername.equals("tedyoung")) {
                        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
                    } else if (githubLoginUsername.equals("nipafx")) {
                        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
                    }
                }
            });

            return mappedAuthorities;
        };
    }

}

