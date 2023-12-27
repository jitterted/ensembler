package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class EnsembleMemberStateTest {

    @Test
    void newEnsembleHasZeroParticipants() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();

        assertThat(ensemble.acceptedCount())
                .isZero();
        assertThat(ensemble.acceptedMembers())
                .isEmpty();
        assertThat(ensemble.spectators())
                .isEmpty();
    }

    @Test
    void unknownMemberThenMemberStatusIsUnknown() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(42);

        assertThat(ensemble.memberStatusFor(memberId))
                .isEqualByComparingTo(MemberStatus.UNKNOWN);
    }

    @Test
    void unknownMemberDeclinesThenMemberStatusIsDeclined() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(97);

        ensemble.declinedBy(memberId);

        assertThat(ensemble.memberStatusFor(memberId))
                .isEqualByComparingTo(MemberStatus.DECLINED);
    }

    @Test
    void joinAsSpectatorThenMemberIsInSpectatorsAndIsSpectatorState() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(123);

        ensemble.joinAsSpectator(memberId);

        assertThat(ensemble.spectators())
                .containsExactly(memberId);
        assertThat(ensemble.memberStatusFor(memberId))
                .isEqualByComparingTo(MemberStatus.SPECTATOR);
    }

    @Test
    void acceptedMemberWhenJoinAsSpectatorRemovesFromAccepted() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(123);
        ensemble.joinAsParticipant(memberId);

        ensemble.joinAsSpectator(memberId);

        assertThat(ensemble.acceptedMembers())
                .isEmpty();
        assertThat(ensemble.spectators())
                .containsExactly(memberId);
    }

    @Test
    void declinedMemberWhenJoinAsSpectatorRemovesFromDeclined() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(123);
        ensemble.declinedBy(memberId);

        ensemble.joinAsSpectator(memberId);

        assertThat(ensemble.declinedMembers())
                .isEmpty();
        assertThat(ensemble.spectators())
                .containsExactly(memberId);
    }

    @Test
    void spectatorWhenAcceptRemovesFromSpectators() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(123);
        ensemble.joinAsSpectator(memberId);

        ensemble.joinAsParticipant(memberId);

        assertThat(ensemble.spectators())
                .isEmpty();
        assertThat(ensemble.acceptedMembers())
                .containsExactly(memberId);
    }

    @Test
    void spectatorWhenDeclineRemovesFromSpectators() {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(123);
        ensemble.joinAsSpectator(memberId);

        ensemble.declinedBy(memberId);

        assertThat(ensemble.spectators())
                .isEmpty();
        assertThat(ensemble.declinedMembers())
                .containsExactly(memberId);
    }

    @Test
    void acceptMemberThenEnsembleTracksMemberAsParticipant() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(123);

        ensemble.joinAsParticipant(memberId);

        assertThat(ensemble.acceptedCount())
                .isEqualTo(1);
        assertThat(ensemble.acceptedMembers())
                .containsOnly(memberId);
        assertThat(ensemble.memberStatusFor(memberId))
                .isEqualByComparingTo(MemberStatus.PARTICIPANT);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    void acceptingMultipleMembersResultsInThatManyRegisteredMembers(int count) throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();

        EnsembleFactory.acceptCountMembersFor(count, ensemble);

        assertThat(ensemble.acceptedCount())
                .isEqualTo(count);
    }

    @ParameterizedTest
    @ValueSource(ints = {6, 7})
    void attemptingToRegisterMoreThanFiveMembersThrowsException(int count) throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();

        assertThatThrownBy(() -> {
            EnsembleFactory.acceptCountMembersFor(count, ensemble);
        }).isInstanceOf(EnsembleFullException.class);
    }

}
