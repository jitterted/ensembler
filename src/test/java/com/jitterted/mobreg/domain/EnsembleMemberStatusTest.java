package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleMemberStatusTest {
    private static final ZonedDateTime UTC_2021_11_22_12 = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 12);

    @Test
    public void unknownMemberAndPastEnsembleThenStatusHidden() throws Exception {
        ZonedDateTime startDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 11);
        Ensemble pastEnsemble = EnsembleFactory.withStartTime(startDateTime);
        MemberId memberId = MemberId.of(37);

        assertThat(pastEnsemble.statusFor(memberId, startDateTime.plusHours(3))) // duration defaults to 1h55m, so 3 hours means it's over
                .isEqualByComparingTo(MemberStatus.HIDDEN);
    }

    @Test
    public void unknownMemberAndFutureEnsembleAndHasSpaceThenStatusUnknown() throws Exception {
        Ensemble futureEnsemble = EnsembleFactory.withStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberId.of(31);

        assertThat(futureEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                .isEqualByComparingTo(MemberStatus.UNKNOWN);
    }

    @Test
    public void unknownMemberAndFutureEnsembleAndIsFullThenStatusFull() throws Exception {
        Ensemble futureEnsemble = EnsembleFactory.ensembleAtCapacityWithStartTime(2022, 1, 3, 9);
        MemberId memberIdIsUnknown = MemberId.of(33);

        assertThat(futureEnsemble.statusFor(memberIdIsUnknown, UTC_2021_11_22_12))
                .isEqualByComparingTo(MemberStatus.FULL);
    }

    @Test
    public void declinedMemberAndFutureEnsembleAndHasSpaceThenStatusDeclined() throws Exception {
        Ensemble futureEnsemble = EnsembleFactory.withStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberId.of(31);
        futureEnsemble.declinedBy(memberId);

        assertThat(futureEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                .isEqualByComparingTo(MemberStatus.DECLINED);
    }

    @Test
    public void declinedMemberAndFutureEnsembleIsFullThenStatusDeclinedFull() throws Exception {
        Ensemble futureFullEnsemble = EnsembleFactory.ensembleAtCapacityWithStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberId.of(31);
        futureFullEnsemble.declinedBy(memberId);

        assertThat(futureFullEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                .isEqualByComparingTo(MemberStatus.DECLINED_FULL);
    }

    @Test
    public void acceptedMemberAndPastUncompletedEnsembleThenStatusPendingCompleted() throws Exception {
        Ensemble pastEnsemble = EnsembleFactory.withStartTime(2021, 11, 21, 11);
        MemberId memberId = MemberId.of(41);
        pastEnsemble.acceptedBy(memberId);

        assertThat(pastEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                .isEqualByComparingTo(MemberStatus.PENDING_COMPLETED);
    }
    
    @Test
    public void acceptedMemberAndCompletedEnsembleThenStatusCompleted() throws Exception {
        Ensemble completedEnsemble = EnsembleFactory.withStartTime(2021, 11, 21, 11);
        MemberId memberId = MemberId.of(41);
        completedEnsemble.acceptedBy(memberId);
        completedEnsemble.complete();

        assertThat(completedEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                .isEqualByComparingTo(MemberStatus.COMPLETED);
    }

    @Test
    public void acceptedMemberAndFutureEnsembleThenStatusAccepted() throws Exception {
        Ensemble futureEnsemble = EnsembleFactory.withStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberId.of(41);
        futureEnsemble.acceptedBy(memberId);

        assertThat(futureEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                .isEqualByComparingTo(MemberStatus.ACCEPTED);
    }

    @Test
    public void acceptedMemberAndInProgressEnsembleThenStatusAccepted() throws Exception {
        ZonedDateTime startDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 11);
        Ensemble futureEnsemble = EnsembleFactory.withStartTime(startDateTime);
        MemberId memberId = MemberId.of(41);
        futureEnsemble.acceptedBy(memberId);

        ZonedDateTime currentDateTime = startDateTime.plusHours(1).plusMinutes(50); // duration is 1h55m, so this ensemble has 5 minutes to go
        assertThat(futureEnsemble.statusFor(memberId, currentDateTime))
                .isEqualByComparingTo(MemberStatus.ACCEPTED);
    }

}