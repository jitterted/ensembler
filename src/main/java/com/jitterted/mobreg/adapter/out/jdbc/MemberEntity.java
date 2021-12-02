package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.time.ZoneId;
import java.util.Set;

class MemberEntity {
    @Id
    Long id;

    String firstName;
    String githubUsername;
    String email;
    String timeZone;

    @MappedCollection
    Set<String> roles;

    static MemberEntity from(Member member) {
        MemberEntity memberEntity = new MemberEntity();
        if (member.getId() != null) {
            memberEntity.id = member.getId().id();
        }
        memberEntity.firstName = member.firstName();
        memberEntity.githubUsername = member.githubUsername();
        memberEntity.email = member.email();
        memberEntity.timeZone = member.timeZone().getId();
        memberEntity.roles = member.roles();
        return memberEntity;
    }

    Member asMember() {
        Member member = new Member(firstName, githubUsername, roles.toArray(new String[0]));
        member.changeEmailTo(email);
        member.changeTimeZoneTo(ZoneId.of(timeZone));
        member.setId(MemberId.of(id));
        return member;
    }

}
