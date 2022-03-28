package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Member;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;

public class TestMemberBuilder {
    private final MemberRepository memberRepository;
    private String firstName = "first";
    private String githubUsername = "githubusername";
    private String email = "first.last@example.com";
    private String[] roles = {"ROLE_USER", "ROLE_MEMBER"};
    private MemberService memberService;
    private String timeZoneId = "Z";

    public TestMemberBuilder() {
        memberRepository = new InMemoryMemberRepository();
        memberService = new DefaultMemberService(memberRepository);
    }

    @NotNull
    public TestMemberBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public TestMemberBuilder withGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
        return this;
    }

    public TestMemberBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public TestMemberBuilder withNoEmail() {
        email = "";
        return this;
    }

    public MemberRepository memberRepository() {
        return memberRepository;
    }

    public MemberService memberService() {
        return memberService;
    }

    public Member buildAndSave() {
        Member member = new Member(firstName, githubUsername, roles);
        member.changeEmailTo(email);
        member.changeTimeZoneTo(ZoneId.of(timeZoneId));
        member = memberRepository.save(member);
        return member;
    }

    public TestMemberBuilder withTimezone(String timeZoneId) {
        this.timeZoneId = timeZoneId;
        return this;
    }

    public void createAnsSaveMembers(int count) {
        for (int i = 0; i < count; i++) {
            Member member = new Member("name" + i, "github" + i);
            memberRepository.save(member);
        }
    }
}
