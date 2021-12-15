package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class MemberLookup {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberLookup.class);


    private final MemberService memberService;

    public MemberLookup(MemberService memberService) {
        this.memberService = memberService;
    }

    Member findMemberBy(AuthenticatedPrincipal principal) {
        requireAsOAuth2User(principal);

        String username = ((OAuth2User) principal).getAttribute("login");

        return memberService.findByGithubUsername(username);
    }

    private void requireAsOAuth2User(AuthenticatedPrincipal principal) {
        if (!(principal instanceof OAuth2User)) {
            LOGGER.warn("AuthenticatedPrincipal expected to be OAuth2User, but was {}", principal);
            throw new IllegalStateException("Not an OAuth2User");
        }
    }
}