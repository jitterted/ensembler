package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZoneId;
import java.util.Set;

@Table("members")
class MemberDbo {
    @Id
    Long id;

    String firstName;
    String githubUsername;
    String email;
    String timeZone;

    @MappedCollection
    Set<String> roles;

    static MemberDbo from(Member member) {
        MemberDbo memberDbo = new MemberDbo();
        if (member.getId() != null) {
            memberDbo.id = member.getId().id();
        }
        memberDbo.firstName = member.firstName();
        memberDbo.githubUsername = member.githubUsername();
        memberDbo.email = member.email();
        memberDbo.timeZone = member.timeZone().getId();
        memberDbo.roles = member.roles();
        return memberDbo;
    }

    Member asMember() {
        Member member = new Member(firstName, githubUsername, roles.toArray(new String[0]));
        member.changeEmailTo(email);
        member.changeTimeZoneTo(ZoneId.of(timeZone));
        member.setId(MemberId.of(id));
        return member;
    }

}
