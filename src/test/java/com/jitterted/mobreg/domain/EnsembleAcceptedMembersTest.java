package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.application.MemberFactory;
import com.jitterted.mobreg.application.TestMemberBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

public class EnsembleAcceptedMembersTest {

    @Test
    public void newEnsembleHasZeroParticipants() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();

        assertThat(ensemble.acceptedCount())
                .isZero();
        assertThat(ensemble.acceptedMembers())
                .isEmpty();
    }

    @Test
    public void acceptMemberByIdWithEnsembleRemembersTheMember() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(123);

        ensemble.acceptedBy(memberId);

        assertThat(ensemble.acceptedCount())
                .isEqualTo(1);

        assertThat(ensemble.acceptedMembers())
                .containsOnly(memberId);
    }

    @Test
    public void acceptedMemberIsFoundAsRegisteredByMemberId() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = new TestMemberBuilder().buildAndSave().getId();

        ensemble.acceptedBy(memberId);

        assertThat(ensemble.isAccepted(memberId))
                .isTrue();
    }

    @Test
    public void nonExistentMemberIsNotFoundAsRegisteredByMemberId() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();

        assertThat(ensemble.isAccepted(MemberId.of(73L)))
                .isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    public void acceptingMultipleMembersResultsInThatManyRegisteredMembers(int count) throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();

        MemberFactory.acceptCountMembersWith(ensemble, count);

        assertThat(ensemble.acceptedCount())
                .isEqualTo(count);
    }

    @ParameterizedTest
    @ValueSource(ints = {6, 7})
    public void attemptingToRegisterMoreThanFiveMembersThrowsException(int count) throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();

        assertThatThrownBy(() -> {
            MemberFactory.acceptCountMembersWith(ensemble, count);
        }).isInstanceOf(EnsembleFullException.class);
    }

}
