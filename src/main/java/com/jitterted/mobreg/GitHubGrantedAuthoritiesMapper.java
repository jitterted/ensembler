package com.jitterted.mobreg;

import com.jitterted.mobreg.domain.MemberService;
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
import java.util.Set;

@Component
public class GitHubGrantedAuthoritiesMapper implements GrantedAuthoritiesMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubGrantedAuthoritiesMapper.class);

    private final MemberService memberService;

    @Autowired
    public GitHubGrantedAuthoritiesMapper(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

        authorities.forEach(authority -> {
            if (authority instanceof OAuth2UserAuthority oauth2UserAuthority) {
                Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();
                requireFromGitHub(userAttributes);
                String githubLoginUsername = (String) userAttributes.get("login");
                LOGGER.info("Checking Membership for login='{}'", githubLoginUsername);
                LOGGER.info("Other user attributes: {}", userAttributes);
                if (githubLoginUsername.equals("tedyoung")) {
                    LOGGER.info("Ted is an ADMIN");
                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
                } else if (memberService.isMember(githubLoginUsername)) {
                    LOGGER.info("'{}' IS a Member", githubLoginUsername);
                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
                } else {
                    LOGGER.info("'{}' is NOT a Member.", githubLoginUsername);
                }
            }
        });

        return mappedAuthorities;
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
