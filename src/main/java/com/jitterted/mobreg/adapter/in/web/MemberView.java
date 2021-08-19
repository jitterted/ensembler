package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.Member;

public record MemberView(Long id,
                         String firstName,
                         String githubUsername,
                         String roles) {
    public static MemberView from(Member member) {
        return new MemberView(member.getId().id(),
                              member.firstName(),
                              member.githubUsername(),
                              String.join(",", member.roles()));
    }
}
