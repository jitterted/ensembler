package com.jitterted.mobreg.application;

import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.jetbrains.annotations.NotNull;

public class MemberFactory {

    @NotNull
    public static Member createMember(long id, String firstName, String githubUsername) {
        Member member = new Member(firstName, githubUsername);
        member.setId(MemberId.of(id));
        return member;
    }

    public static Member createMember(long id, String firstName, String githubUsername, String... roles) {
        Member member = new Member(firstName, githubUsername, roles);
        member.setId(MemberId.of(id));
        return member;
    }
}
