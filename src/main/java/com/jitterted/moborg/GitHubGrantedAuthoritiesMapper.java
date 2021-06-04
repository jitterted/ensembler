package com.jitterted.moborg;

import com.jitterted.moborg.domain.MemberService;
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
                if (githubLoginUsername.equals("tedyoung")) {
                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
                } else if (memberService.isMember(githubLoginUsername)) {
                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
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
