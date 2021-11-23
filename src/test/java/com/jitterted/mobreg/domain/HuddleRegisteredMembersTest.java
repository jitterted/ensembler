package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

public class HuddleRegisteredMembersTest {

    @Test
    public void newHuddleHasZeroParticipants() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();

        assertThat(huddle.registeredMemberCount())
                .isZero();
        assertThat(huddle.registeredMembers())
                .isEmpty();
    }

    @Test
    public void registerMemberByIdWithHuddleRemembersTheMember() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();
        MemberId memberId = new MemberBuilder().build().getId();

        huddle.acceptedBy(memberId);

        assertThat(huddle.registeredMemberCount())
                .isEqualTo(1);

        assertThat(huddle.registeredMembers())
                .containsOnly(memberId);
    }

    @Test
    public void registeredMemberIsFoundAsRegisteredByMemberId() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();
        MemberId memberId = new MemberBuilder().build().getId();

        huddle.acceptedBy(memberId);

        assertThat(huddle.isRegistered(memberId))
                .isTrue();
    }

    @Test
    public void nonExistentMemberIsNotFoundAsRegisteredByMemberId() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();

        assertThat(huddle.isRegistered(MemberId.of(73L)))
                .isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    public void registeringMultipleMembersResultsInThatManyRegisteredMembers(int count) throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();

        MemberFactory.registerCountMembersWithHuddle(huddle, count);

        assertThat(huddle.registeredMemberCount())
                .isEqualTo(count);
    }

    @ParameterizedTest
    @ValueSource(ints = {6, 7})
    public void attemptingToRegisterMoreThanFiveMembersThrowsException(int count) throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();

        assertThatThrownBy(() -> {
            MemberFactory.registerCountMembersWithHuddle(huddle, count);
        }).isInstanceOf(HuddleIsAlreadyFullException.class);
    }

}
