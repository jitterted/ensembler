package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.application.MemberFactory;
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
        Huddle futureHuddle = HuddleFactory.fullHuddleWithStartTime(2022, 1, 3, 9);
        MemberId memberIdIsUnknown = MemberFactory.createMember(33L, "UnKnown", "unknown").getId();

        assertThat(futureHuddle.statusFor(memberIdIsUnknown, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.FULL);
    }

    @Test
    public void declinedMemberAndFutureHuddleAndHasSpaceThenStatusDeclined() throws Exception {
        Huddle futureHuddle = HuddleFactory.withStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberFactory.createMember(31L, "Declined", "declined").getId();
        futureHuddle.declinedBy(memberId);

        assertThat(futureHuddle.statusFor(memberId, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.DECLINED);
    }

    @Test
    public void declinedMemberAndFutureHuddleIsFullThenStatusDeclinedFull() throws Exception {
        Huddle futureFullHuddle = HuddleFactory.fullHuddleWithStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberFactory.createMember(31L, "Declined", "declined").getId();
        futureFullHuddle.declinedBy(memberId);

        assertThat(futureFullHuddle.statusFor(memberId, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.DECLINED_FULL);
    }

    @Test
    public void acceptedMemberAndPastUncompletedHuddleThenStatusPendingCompleted() throws Exception {
        Huddle pastHuddle = HuddleFactory.withStartTime(2021, 11, 21, 11);
        MemberId memberId = MemberFactory.createMember(41L, "Accepted", "accepted").getId();
        pastHuddle.acceptedBy(memberId);

        assertThat(pastHuddle.statusFor(memberId, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.PENDING_COMPLETED);
    }
    
    @Test
    public void acceptedMemberAndCompletedHuddleThenStatusCompleted() throws Exception {
        Huddle completedHuddle = HuddleFactory.withStartTime(2021, 11, 21, 11);
        MemberId memberId = MemberFactory.createMember(41L, "Accepted", "accepted").getId();
        completedHuddle.acceptedBy(memberId);
        completedHuddle.complete();

        assertThat(completedHuddle.statusFor(memberId, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.COMPLETED);
    }

    @Test
    public void acceptedMemberAndFutureHuddleThenStatusAccepted() throws Exception {
        Huddle futureHuddle = HuddleFactory.withStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberFactory.createMember(41L, "Accepted", "accepted").getId();
        futureHuddle.acceptedBy(memberId);

        assertThat(futureHuddle.statusFor(memberId, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.ACCEPTED);
    }

}