package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class GitHubGrantedAuthoritiesMapper implements GrantedAuthoritiesMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubGrantedAuthoritiesMapper.class);

    private final MemberRepository memberRepository;

    @Autowired
    public GitHubGrantedAuthoritiesMapper(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

        authorities.forEach(authority -> {
            if (authority instanceof OAuth2UserAuthority oauth2UserAuthority) {
                Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();
                requireFromGitHub(userAttributes);
                String githubLoginUsername = (String) userAttributes.get("login");
                LOGGER.debug("Looking up GitHub Username `{}` in Member Repository as {}", githubLoginUsername, githubLoginUsername.toLowerCase());
                Optional<Member> memberOpt = memberRepository.findByGithubUsername(githubLoginUsername.toLowerCase());
                if (memberOpt.isPresent()) {
                    LOGGER.debug("{} found: assigning {} roles", githubLoginUsername, memberOpt.get().roles());
                    mapToRoleSet(mappedAuthorities, memberOpt.get());
                } else {
                    LOGGER.debug("{} not found in database, assigning ROLE_USER only", githubLoginUsername);
                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                }
            }

        });

        return mappedAuthorities;
    }

    private void mapToRoleSet(Set<GrantedAuthority> mappedAuthorities, Member member) {
        member.roles().stream()
              .map(SimpleGrantedAuthority::new)
              .forEach(mappedAuthorities::add);
    }

    private void requireFromGitHub(Map<String, Object> userAttributes) {
        if (userAttributes.containsKey("url")) {
            String url = (String) userAttributes.get("url");
            if (!url.contains("github.com")) {
                throw new IllegalArgumentException("Unexpected source for Authority, URL: " + url);
            }
        }
    }
}
