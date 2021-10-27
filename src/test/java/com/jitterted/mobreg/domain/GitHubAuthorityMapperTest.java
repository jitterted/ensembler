package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.GitHubGrantedAuthoritiesMapper;
import com.jitterted.mobreg.domain.port.InMemoryMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class GitHubAuthorityMapperTest {

    @Test
    public void usernameFoundInMemberRepositoryGetsRolesFromMember() throws Exception {
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();
        Member member = new Member("Member", "member_username", "ROLE_MEMBER", "ROLE_USER");
        memberRepository.save(member);

        GitHubGrantedAuthoritiesMapper gitHubGrantedAuthoritiesMapper =
                new GitHubGrantedAuthoritiesMapper(memberRepository);
        OAuth2UserAuthority oauth2UserAuthority = OAuth2UserFactory.createGitHubUserWithLogin("member_username");

        var grantedAuthorities =
                gitHubGrantedAuthoritiesMapper.mapAuthorities(List.of(oauth2UserAuthority));

        assertThat(grantedAuthorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsOnly("ROLE_MEMBER", "ROLE_USER");
    }

    @Test
    public void usernameNotInMemberRepositoryHasOnlyUserRole() throws Exception {
        InMemoryMemberRepository memberRepository = new InMemoryMemberRepository();

        GitHubGrantedAuthoritiesMapper gitHubGrantedAuthoritiesMapper =
                new GitHubGrantedAuthoritiesMapper(memberRepository);

        var grantedAuthorities = gitHubGrantedAuthoritiesMapper
                .mapAuthorities(List.of(OAuth2UserFactory.createGitHubUserWithLogin("non_member")));

        assertThat(grantedAuthorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsOnly("ROLE_USER");
    }

}