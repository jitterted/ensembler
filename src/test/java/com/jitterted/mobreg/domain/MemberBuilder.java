package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.port.MemberRepository;
import org.jetbrains.annotations.NotNull;

public class MemberBuilder {
    private final MemberRepository memberRepository = new InMemoryMemberRepository();
    private String firstName = "first";
    private String githubUsername = "githubusername";
    private String email = "first.last@example.com";
    private String[] roles = {"ROLE_USER", "ROLE_MEMBER"};
    private MemberService memberService;

    @NotNull
    public MemberBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public MemberBuilder withGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
        return this;
    }

    public MemberBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public MemberBuilder withNoEmail() {
        email = "";
        return this;
    }

    public MemberRepository memberRepository() {
        return memberRepository;
    }

    public MemberService memberService() {
        return memberService;
    }

    public Member build() {
        Member member = new Member(firstName, githubUsername, roles);
        member.changeEmailTo(email);
        member = memberRepository.save(member);
        memberService = new MemberService(memberRepository);
        return member;
    }
}
