package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleMemberStatusTest {
    private static final ZonedDateTime UTC_2021_11_22_15 = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 15);

    @Test
    public void unknownMemberAndPastHuddleThenStatusHidden() throws Exception {
        Huddle pastHuddle = HuddleFactory.withStartTime(2021, 11, 22, 11);
        MemberId memberId = MemberFactory.createMember(37L, "UnKnown", "unknown").getId();

        assertThat(pastHuddle.statusFor(memberId, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.HIDDEN);
    }

    @Test
    public void unknownMemberAndFutureHuddleAndHasSpaceThenStatusUnknown() throws Exception {
        Huddle futureHuddle = HuddleFactory.withStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberFactory.createMember(31L, "UnKnown", "unknown").getId();

        assertThat(futureHuddle.statusFor(memberId, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.UNKNOWN);
    }

    @Test
    public void unknownMemberAndFutureHuddleAndIsFullThenStatusFull() throws Exception {
        Huddle futureHuddle = HuddleFactory.withStartTime(2022, 1, 3, 9);
        MemberFactory.registerCountMembersWithHuddle(futureHuddle, 5);
        MemberId memberIdIsUnknown = MemberFactory.createMember(33L, "UnKnown", "unknown").getId();

        assertThat(futureHuddle.statusFor(memberIdIsUnknown, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.FULL);
    }
}