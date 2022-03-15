package com.jitterted.mobreg.adapter.in.web;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GitHubUsernamePrincipalExtractor {
    @NotNull
    public static String usernameFrom(AuthenticatedPrincipal principal) {
        String username = ((OAuth2User) principal).getAttribute("login");
        if (username == null) {
            throw new IllegalStateException("Username null for " + principal);
        }
        return username;
    }
}
