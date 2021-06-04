package com.jitterted.moborg.domain;

import com.jitterted.moborg.GitHubGrantedAuthoritiesMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class GitHubAuthorityMapperTest {

    @Test
    public void gitHubCollaboratorIsMember() throws Exception {
        GitHubGrantedAuthoritiesMapper gitHubGrantedAuthoritiesMapper =
                new GitHubGrantedAuthoritiesMapper(username -> username.equals("collaborator"));
        OAuth2UserAuthority oauth2UserAuthority = createGitHubUserWithLogin("collaborator");

        var grantedAuthorities =
                gitHubGrantedAuthoritiesMapper.mapAuthorities(List.of(oauth2UserAuthority));

        assertThat(grantedAuthorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsOnly("ROLE_MEMBER");
    }

    @Test
    public void notGitHubCollaboratorHasNoGrantedAuthorities() throws Exception {
        GitHubGrantedAuthoritiesMapper gitHubCollaboratorMapper =
                new GitHubGrantedAuthoritiesMapper(username -> username.equals("collaborator"));

        var grantedAuthorities = gitHubCollaboratorMapper
                .mapAuthorities(List.of(createGitHubUserWithLogin("noncollaborator")));

        assertThat(grantedAuthorities)
                .isEmpty();
    }

    @NotNull
    private OAuth2UserAuthority createGitHubUserWithLogin(String loginUsername) {
        return new OAuth2UserAuthority(
                Map.of("url", "https://api.github.com/",
                       "login", loginUsername));
    }

}