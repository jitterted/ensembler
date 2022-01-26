package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EnsembleMemberRsvpTest {

    @Test
    public void unregisteredMemberIsRsvpUnknown() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(73);

        assertThat(ensemble.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.UNKNOWN);
    }

    @Test
    public void registeredMemberIsRsvpAccepted() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(73);

        ensemble.acceptedBy(memberId);

        assertThat(ensemble.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.ACCEPTED);
    }

    @Test
    public void memberWhoDeclinesIsRsvpDeclined() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(97);

        ensemble.declinedBy(memberId);

        assertThat(ensemble.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.DECLINED);
    }

    @Test
    public void acceptedMemberWhenDeclinesIsRsvpDeclined() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(97);
        ensemble.acceptedBy(memberId);

        ensemble.declinedBy(memberId);

        assertThat(ensemble.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.DECLINED);
    }

    @Test
    public void declinedMemberWhenAcceptsAndSpaceAvailableIsRsvpAccepted() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        MemberId memberId = MemberId.of(79);
        ensemble.declinedBy(memberId);

        ensemble.acceptedBy(memberId);

        assertThat(ensemble.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.ACCEPTED);
    }

}