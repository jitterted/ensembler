package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.springframework.data.annotation.Id;

public class MemberEntity {
    @Id
    private Long id;

    private String firstName;
    private String githubUsername;

    public static MemberEntity from(Member member) {
        MemberEntity memberEntity = new MemberEntity();
        if (member.getId() != null) {
            memberEntity.setId(member.getId().id());
        }
        memberEntity.setFirstName(member.firstName());
        memberEntity.setGithubUsername(member.githubUsername());
        return memberEntity;
    }

    public Member asMember() {
        Member member = new Member(firstName, githubUsername);
        member.setId(MemberId.of(id));
        return member;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
