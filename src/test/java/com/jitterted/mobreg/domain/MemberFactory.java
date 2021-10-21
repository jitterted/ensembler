package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.MemberRepository;
import org.jetbrains.annotations.NotNull;

public class MemberFactory {

    private final MemberRepository memberRepository;

    public MemberFactory(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberFactory() {
        this.memberRepository = new InMemoryMemberRepository();
    }

    @NotNull
    public static Member createMember(long id, String name, String githubUsername) {
        Member member = new Member(name, githubUsername);
        member.setId(MemberId.of(id));
        return member;
    }

    @NotNull
    public MemberId createMemberInRepositoryReturningId(long id, String name, String githubUsername) {
        Member member = createMember(id, name, githubUsername);
        memberRepository.save(member);
        return member.getId();
    }

    public MemberId createMemberInRepositoryReturningId(long id, String name, String githubUsername, String email) {
        Member member = createMember(id, name, githubUsername);
        member.changeEmailTo(email);
        memberRepository.save(member);
        return member.getId();
    }
}
