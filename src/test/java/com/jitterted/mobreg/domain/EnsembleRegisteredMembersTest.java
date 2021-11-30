package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.application.MemberBuilder;
import com.jitterted.mobreg.application.MemberFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

public class EnsembleRegisteredMembersTest {

    @Test
    public void newEnsembleHasZeroParticipants() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();

        assertThat(ensemble.acceptedCount())
                .isZero();
        assertThat(ensemble.acceptedMembers())
                .isEmpty();
    }

    @Test
    public void registerMemberByIdWithEnsembleRemembersTheMember() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = new MemberBuilder().build().getId();

        ensemble.acceptedBy(memberId);

        assertThat(ensemble.acceptedCount())
                .isEqualTo(1);

        assertThat(ensemble.acceptedMembers())
                .containsOnly(memberId);
    }

    @Test
    public void registeredMemberIsFoundAsRegisteredByMemberId() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = new MemberBuilder().build().getId();

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
    public void registeringMultipleMembersResultsInThatManyRegisteredMembers(int count) throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();

        MemberFactory.registerCountMembersWith(ensemble, count);

        assertThat(ensemble.acceptedCount())
                .isEqualTo(count);
    }

    @ParameterizedTest
    @ValueSource(ints = {6, 7})
    public void attemptingToRegisterMoreThanFiveMembersThrowsException(int count) throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();

        assertThatThrownBy(() -> {
            MemberFactory.registerCountMembersWith(ensemble, count);
        }).isInstanceOf(EnsembleFullException.class);
    }

}
