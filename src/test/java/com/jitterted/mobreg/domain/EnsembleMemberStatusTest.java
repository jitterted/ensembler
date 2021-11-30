package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.application.MemberFactory;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleMemberStatusTest {
    private static final ZonedDateTime UTC_2021_11_22_15 = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 15);

    @Test
    public void unknownMemberAndPastHuddleThenStatusHidden() throws Exception {
        Ensemble pastEnsemble = HuddleFactory.withStartTime(2021, 11, 22, 11);
        MemberId memberId = MemberFactory.createMember(37L, "UnKnown", "unknown").getId();

        assertThat(pastEnsemble.statusFor(memberId, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.HIDDEN);
    }

    @Test
    public void unknownMemberAndFutureHuddleAndHasSpaceThenStatusUnknown() throws Exception {
        Ensemble futureEnsemble = HuddleFactory.withStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberFactory.createMember(31L, "UnKnown", "unknown").getId();

        assertThat(futureEnsemble.statusFor(memberId, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.UNKNOWN);
    }

    @Test
    public void unknownMemberAndFutureHuddleAndIsFullThenStatusFull() throws Exception {
        Ensemble futureEnsemble = HuddleFactory.fullHuddleWithStartTime(2022, 1, 3, 9);
        MemberId memberIdIsUnknown = MemberFactory.createMember(33L, "UnKnown", "unknown").getId();

        assertThat(futureEnsemble.statusFor(memberIdIsUnknown, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.FULL);
    }

    @Test
    public void declinedMemberAndFutureHuddleAndHasSpaceThenStatusDeclined() throws Exception {
        Ensemble futureEnsemble = HuddleFactory.withStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberFactory.createMember(31L, "Declined", "declined").getId();
        futureEnsemble.declinedBy(memberId);

        assertThat(futureEnsemble.statusFor(memberId, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.DECLINED);
    }

    @Test
    public void declinedMemberAndFutureHuddleIsFullThenStatusDeclinedFull() throws Exception {
        Ensemble futureFullEnsemble = HuddleFactory.fullHuddleWithStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberFactory.createMember(31L, "Declined", "declined").getId();
        futureFullEnsemble.declinedBy(memberId);

        assertThat(futureFullEnsemble.statusFor(memberId, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.DECLINED_FULL);
    }

    @Test
    public void acceptedMemberAndPastUncompletedHuddleThenStatusPendingCompleted() throws Exception {
        Ensemble pastEnsemble = HuddleFactory.withStartTime(2021, 11, 21, 11);
        MemberId memberId = MemberFactory.createMember(41L, "Accepted", "accepted").getId();
        pastEnsemble.acceptedBy(memberId);

        assertThat(pastEnsemble.statusFor(memberId, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.PENDING_COMPLETED);
    }
    
    @Test
    public void acceptedMemberAndCompletedHuddleThenStatusCompleted() throws Exception {
        Ensemble completedEnsemble = HuddleFactory.withStartTime(2021, 11, 21, 11);
        MemberId memberId = MemberFactory.createMember(41L, "Accepted", "accepted").getId();
        completedEnsemble.acceptedBy(memberId);
        completedEnsemble.complete();

        assertThat(completedEnsemble.statusFor(memberId, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.COMPLETED);
    }

    @Test
    public void acceptedMemberAndFutureHuddleThenStatusAccepted() throws Exception {
        Ensemble futureEnsemble = HuddleFactory.withStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberFactory.createMember(41L, "Accepted", "accepted").getId();
        futureEnsemble.acceptedBy(memberId);

        assertThat(futureEnsemble.statusFor(memberId, UTC_2021_11_22_15))
                .isEqualByComparingTo(MemberStatus.ACCEPTED);
    }

}