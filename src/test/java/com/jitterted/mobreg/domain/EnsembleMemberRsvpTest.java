package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EnsembleMemberRsvpTest {

    @Test
    void unregisteredMemberIsRsvpUnknown() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(73);

        assertThat(ensemble.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.UNKNOWN);
    }

    @Test
    void registeredMemberIsRsvpAccepted() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(73);

        ensemble.joinAsParticipant(memberId);

        assertThat(ensemble.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.ACCEPTED);
    }

    @Test
    void memberWhoDeclinesIsRsvpDeclined() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(97);

        ensemble.declinedBy(memberId);

        assertThat(ensemble.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.DECLINED);
    }

    @Test
    void acceptedMemberWhenDeclinesIsRsvpDeclined() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(97);
        ensemble.joinAsParticipant(memberId);

        ensemble.declinedBy(memberId);

        assertThat(ensemble.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.DECLINED);
    }

    @Test
    void declinedMemberWhenAcceptsAndSpaceAvailableIsRsvpAccepted() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(79);
        ensemble.declinedBy(memberId);

        ensemble.joinAsParticipant(memberId);

        assertThat(ensemble.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.ACCEPTED);
    }

}