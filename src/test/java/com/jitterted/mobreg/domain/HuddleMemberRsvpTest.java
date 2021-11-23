package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class HuddleMemberRsvpTest {

    @Test
    public void unregisteredMemberIsRsvpUnknown() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();
        MemberId memberId = MemberFactory.createMember(73L, "Seventy3", "73").getId();

        assertThat(huddle.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.UNKNOWN);
    }

    @Test
    public void registeredMemberIsRsvpAccepted() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();
        MemberId memberId = MemberFactory.createMember(73L, "Seventy3", "73").getId();

        huddle.acceptedBy(memberId);

        assertThat(huddle.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.ACCEPTED);
    }

    @Test
    public void memberWhoDeclinesIsRsvpDeclined() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();
        MemberId memberId = MemberFactory.createMember(97L, "Ninety7", "97").getId();

        huddle.declinedBy(memberId);

        assertThat(huddle.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.DECLINED);
    }

    @Test
    public void acceptedMemberWhenDeclinesIsRsvpDeclined() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();
        MemberId memberId = MemberFactory.createMember(97L, "Ninety7", "97").getId();
        huddle.acceptedBy(memberId);

        huddle.declinedBy(memberId);

        assertThat(huddle.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.DECLINED);
    }

    @Test
    public void declinedMemberWhenAcceptsAndSpaceAvailableIsRsvpAccepted() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleStartTimeNow();
        MemberId memberId = MemberFactory.createMember(97L, "Ninety7", "97").getId();
        huddle.declinedBy(memberId);

        huddle.acceptedBy(memberId);

        assertThat(huddle.rsvpOf(memberId))
                .isEqualByComparingTo(Rsvp.ACCEPTED);
    }

}