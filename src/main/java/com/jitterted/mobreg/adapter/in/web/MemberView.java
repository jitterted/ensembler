package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.Member;

public record MemberView(String firstName,
                         String githubUsername) {
    public static MemberView from(Member member) {
        return new MemberView(member.firstName(),
                              member.githubUsername());
    }
}
