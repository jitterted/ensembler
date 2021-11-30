package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.application.MemberFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EnsembleMemberRsvpTest {

    @Test
    public void unregisteredMemberIsRsvpUnknown() throws Exception {
        Ensemble ensemble = HuddleFactory.createDefaultHuddleStartTimeNow();
        MemberId memberId = MemberFactory.createMember(73L, "Seventy3", "73").getId();

        assertThat(ensemble.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.UNKNOWN);
    }

    @Test
    public void registeredMemberIsRsvpAccepted() throws Exception {
        Ensemble ensemble = HuddleFactory.createDefaultHuddleStartTimeNow();
        MemberId memberId = MemberFactory.createMember(73L, "Seventy3", "73").getId();

        ensemble.acceptedBy(memberId);

        assertThat(ensemble.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.ACCEPTED);
    }

    @Test
    public void memberWhoDeclinesIsRsvpDeclined() throws Exception {
        Ensemble ensemble = HuddleFactory.createDefaultHuddleStartTimeNow();
        MemberId memberId = MemberFactory.createMember(97L, "Ninety7", "97").getId();

        ensemble.declinedBy(memberId);

        assertThat(ensemble.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.DECLINED);
    }

    @Test
    public void acceptedMemberWhenDeclinesIsRsvpDeclined() throws Exception {
        Ensemble ensemble = HuddleFactory.createDefaultHuddleStartTimeNow();
        MemberId memberId = MemberFactory.createMember(97L, "Ninety7", "97").getId();
        ensemble.acceptedBy(memberId);

        ensemble.declinedBy(memberId);

        assertThat(ensemble.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.DECLINED);
    }

    @Test
    public void declinedMemberWhenAcceptsAndSpaceAvailableIsRsvpAccepted() throws Exception {
        Ensemble ensemble = HuddleFactory.createDefaultHuddleStartTimeNow();
        MemberId memberId = MemberFactory.createMember(97L, "Ninety7", "97").getId();
        ensemble.declinedBy(memberId);

        ensemble.acceptedBy(memberId);

        assertThat(ensemble.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.ACCEPTED);
    }

}