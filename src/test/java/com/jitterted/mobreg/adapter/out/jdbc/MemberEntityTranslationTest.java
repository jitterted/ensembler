package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

public class MemberEntityTranslationTest {

    @Test
    public void memberWithRolesTranslatedToEntityWithRoles() throws Exception {
        Member member = new Member("first", "githubuser", "ROLE_USER", "ROLE_MEMBER");

        MemberEntity memberEntity = MemberEntity.from(member);

        assertThat(memberEntity.roles)
                .containsOnly("ROLE_USER", "ROLE_MEMBER");
    }

    @Test
    public void entityWithRolesTranslatedToMemberWithRoles() throws Exception {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.firstName = "first";
        memberEntity.githubUsername = "githubusername";
        memberEntity.timeZone = "Z";
        memberEntity.roles = Set.of("ROLE_ONE", "ROLE_TWO");
        memberEntity.id = 11L;

        Member member = memberEntity.asMember();

        assertThat(member.roles())
                .containsOnly("ROLE_ONE", "ROLE_TWO");
    }

    @Test
    public void entityWithEmailTranslatedToDomainMemberWithEmail() throws Exception {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.email = "member@example.com";
        memberEntity.firstName = "member";
        memberEntity.githubUsername = "githubusername";
        memberEntity.timeZone = "Z";
        memberEntity.roles = Set.of("ROLE_ONE", "ROLE_TWO");
        memberEntity.id = 31L;

        Member member = memberEntity.asMember();

        assertThat(member.email())
                .isEqualTo("member@example.com");
    }

    @Test
    public void domainMemberWithEmailTranslatedToDataEntityWithEmail() throws Exception {
        Member member = new Member("member", "ghuser", "ROLE_THREE");
        member.changeEmailTo("ghuser@example.com");

        MemberEntity memberEntity = MemberEntity.from(member);

        assertThat(memberEntity.email)
                .isEqualTo("ghuser@example.com");
    }
}
