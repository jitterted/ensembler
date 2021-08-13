package com.jitterted.mobreg.domain;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.List;
import java.util.Map;

public class OAuth2UserFactory {
    @NotNull
    public static DefaultOAuth2User createOAuth2UserWithMemberRole(String githubUsername) {
        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_MEMBER")),
                Map.of("login", githubUsername, "name", "Ted M. Young"),
                "name");
    }
}
