package com.jitterted.mobreg.domain;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

public class HuddleRegisteredMembersTest {

    @Test
    public void newHuddleHasZeroParticipants() throws Exception {
        Huddle huddle = createDefaultHuddleStartTimeNow();

        assertThat(huddle.registeredMemberCount())
                .isZero();
        assertThat(huddle.registeredMembers())
                .isEmpty();
    }

    @Test
    public void registerMemberByIdWithHuddleRemembersTheMember() throws Exception {
        Huddle huddle = createDefaultHuddleStartTimeNow();

        MemberId memberId = new MemberFactory().createMemberInRepositoryReturningId(1L, "name", "github");

        huddle.register(memberId);

        assertThat(huddle.registeredMemberCount())
                .isEqualTo(1);

        assertThat(huddle.registeredMembers())
                .containsOnly(memberId);
    }

    @Test
    public void registeredMemberIsFoundAsRegisteredByMemberId() throws Exception {
        Huddle huddle = createDefaultHuddleStartTimeNow();
        MemberId memberId = new MemberFactory().createMemberInRepositoryReturningId(3L, "reg", "github");
        huddle.register(memberId);

        assertThat(huddle.isRegistered(memberId))
                .isTrue();
    }

    @Test
    public void nonExistentMemberIsNotFoundAsRegisteredByMemberId() throws Exception {
        Huddle huddle = createDefaultHuddleStartTimeNow();

        assertThat(huddle.isRegistered(MemberId.of(73L)))
                .isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    public void registeringMultipleMembersResultsInThatManyRegisteredMembers(int count) throws Exception {
        Huddle huddle = createDefaultHuddleStartTimeNow();

        registerMembersOfCount(huddle, count);

        assertThat(huddle.registeredMemberCount())
                .isEqualTo(count);
    }

    @ParameterizedTest
    @ValueSource(ints = {6, 7})
    public void registeringMoreThanFiveMembersThrowsException(int count) throws Exception {
        Huddle huddle = createDefaultHuddleStartTimeNow();

        assertThatThrownBy(() -> {
            registerMembersOfCount(huddle, count);
        }).isInstanceOf(HuddleIsAlreadyFullException.class);
    }

    private void registerMembersOfCount(Huddle huddle, int count) {
        MemberFactory memberFactory = new MemberFactory();
        for (int i = 0; i < count; i++) {
            MemberId memberId = memberFactory.createMemberInRepositoryReturningId(
                    i, "name" + i, "github" + i);
            huddle.register(memberId);
        }
    }

    @NotNull
    private Huddle createDefaultHuddleStartTimeNow() {
        return new Huddle("huddle", ZonedDateTime.now());
    }

}
