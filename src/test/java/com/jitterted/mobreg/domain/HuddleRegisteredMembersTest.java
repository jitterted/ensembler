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

        MemberId memberId = MemberFactory.createMemberReturningId(1L, "name", "github");

        huddle.registerById(memberId);

        assertThat(huddle.registeredMemberCount())
                .isEqualTo(1);

        assertThat(huddle.registeredMembers())
                .containsOnly(memberId);
    }

    @Test
    public void registeredMemberIsFoundAsRegisteredByMemberId() throws Exception {
        Huddle huddle = new Huddle("huddle", ZonedDateTime.now());
        MemberId memberId = MemberFactory.createMemberReturningId(3L, "reg", "github");
        huddle.registerById(memberId);

        assertThat(huddle.isRegisteredById(memberId))
                .isTrue();
    }

    @Test
    public void nonExistentMemberIsNotFoundAsRegisteredByMemberId() throws Exception {
        Huddle huddle = new Huddle("huddle", ZonedDateTime.now());

        assertThat(huddle.isRegisteredById(MemberId.of(73L)))
                .isFalse();
    }

}
