package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

public class HuddleRegisteredMembersTest {

    @Test
    public void newHuddleHasZeroParticipants() throws Exception {
        Huddle huddle = new Huddle("test", ZonedDateTime.now());

        assertThat(huddle.registeredMemberCount())
                .isZero();
        assertThat(huddle.registeredMembers())
                .isEmpty();
    }

    @Test
    public void registerMemberByIdWithHuddleRemembersTheMember() throws Exception {
        Huddle huddle = new Huddle("huddle", ZonedDateTime.now());

        MemberId memberId = new MemberFactory().createMemberInRepositoryReturningId(1L, "name", "github");

        huddle.register(memberId);

        assertThat(huddle.registeredMemberCount())
                .isEqualTo(1);

        assertThat(huddle.registeredMembers())
                .containsOnly(memberId);
    }

    @Test
    public void registeredMemberIsFoundAsRegisteredByMemberId() throws Exception {
        Huddle huddle = new Huddle("huddle", ZonedDateTime.now());
        MemberId memberId = new MemberFactory().createMemberInRepositoryReturningId(3L, "reg", "github");
        huddle.register(memberId);

        assertThat(huddle.isRegistered(memberId))
                .isTrue();
    }

    @Test
    public void nonExistentMemberIsNotFoundAsRegisteredByMemberId() throws Exception {
        Huddle huddle = new Huddle("huddle", ZonedDateTime.now());

        assertThat(huddle.isRegistered(MemberId.of(73L)))
                .isFalse();
    }

}
