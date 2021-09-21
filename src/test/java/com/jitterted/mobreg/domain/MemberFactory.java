package com.jitterted.mobreg.domain;

import org.jetbrains.annotations.NotNull;

public class MemberFactory {
    @NotNull
    public static MemberId createMemberReturningId(long id, String name, String githubUsername) {
        Member member = createMember(id, name, githubUsername);
        return member.getId();
    }

    @NotNull
    public static Member createMember(long id, String name, String githubUsername) {
        Member member = new Member(name, githubUsername);
        member.setId(MemberId.of(id));
        return member;
    }
}
