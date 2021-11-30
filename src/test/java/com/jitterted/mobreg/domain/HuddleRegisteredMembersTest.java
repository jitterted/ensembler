package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.application.MemberBuilder;
import com.jitterted.mobreg.application.MemberFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

public class HuddleRegisteredMembersTest {

    @Test
    public void newHuddleHasZeroParticipants() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();

        assertThat(huddle.acceptedCount())
                .isZero();
        assertThat(huddle.acceptedMembers())
                .isEmpty();
    }

    @Test
    public void registerMemberByIdWithHuddleRemembersTheMember() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();
        MemberId memberId = new MemberBuilder().build().getId();

        huddle.acceptedBy(memberId);

        assertThat(huddle.acceptedCount())
                .isEqualTo(1);

        assertThat(huddle.acceptedMembers())
                .containsOnly(memberId);
    }

    @Test
    public void registeredMemberIsFoundAsRegisteredByMemberId() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();
        MemberId memberId = new MemberBuilder().build().getId();

        huddle.acceptedBy(memberId);

        assertThat(huddle.isAccepted(memberId))
                .isTrue();
    }

    @Test
    public void nonExistentMemberIsNotFoundAsRegisteredByMemberId() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();

        assertThat(huddle.isAccepted(MemberId.of(73L)))
                .isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    public void registeringMultipleMembersResultsInThatManyRegisteredMembers(int count) throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();

        MemberFactory.registerCountMembersWithHuddle(huddle, count);

        assertThat(huddle.acceptedCount())
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
