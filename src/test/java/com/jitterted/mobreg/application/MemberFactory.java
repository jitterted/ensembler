package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.jetbrains.annotations.NotNull;

public class MemberFactory {

    private final MemberRepository memberRepository;

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

    public static void acceptCountMembersWith(Ensemble ensemble, int count) {
        MemberFactory memberFactory = new MemberFactory();
        for (int i = 0; i < count; i++) {
            MemberId memberId = memberFactory.createMemberInRepositoryReturningId(
                    i, "name" + i, "github" + i);
            ensemble.acceptedBy(memberId);
        }
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
