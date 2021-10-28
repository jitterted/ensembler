package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.InMemoryMemberRepository;
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

    @NotNull
    public MemberId createMemberInRepositoryReturningId(long id, String firstName, String githubUsername) {
        Member member = createMember(id, firstName, githubUsername);
        memberRepository.save(member);
        return member.getId();
    }

    public MemberId createMemberInRepositoryReturningId(long id, String firstName, String githubUsername, String email) {
        Member member = createMember(id, firstName, githubUsername);
        member.changeEmailTo(email);
        memberRepository.save(member);
        return member.getId();
    }
}
