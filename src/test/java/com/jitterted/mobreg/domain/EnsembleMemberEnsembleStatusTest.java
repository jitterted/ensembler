package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleMemberEnsembleStatusTest {
    private static final ZonedDateTime UTC_2021_11_22_12 = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 12);

    @Test
    void unknownMemberAndPastEnsembleThenStatusHidden() throws Exception {
        ZonedDateTime startDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 11);
        Ensemble pastEnsemble = EnsembleFactory.withStartTime(startDateTime);
        MemberId memberId = MemberId.of(37);

        assertThat(pastEnsemble.statusFor(memberId, startDateTime.plusHours(3))) // duration defaults to 1h55m, so 3 hours means it's over
                                                                                 .isEqualByComparingTo(MemberEnsembleStatus.HIDDEN);
    }

    @Test
    void unknownMemberAndFutureEnsembleAndHasSpaceThenStatusUnknown() throws Exception {
        Ensemble futureEnsemble = EnsembleFactory.withStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberId.of(31);

        assertThat(futureEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                .isEqualByComparingTo(MemberEnsembleStatus.UNKNOWN);
    }

    @Test
    void unknownMemberAndFutureEnsembleAndIsFullThenStatusFull() throws Exception {
        Ensemble futureEnsemble = EnsembleFactory.ensembleAtCapacityWithStartTime(2022, 1, 3, 9);
        MemberId memberIdIsUnknown = MemberId.of(33);

        assertThat(futureEnsemble.statusFor(memberIdIsUnknown, UTC_2021_11_22_12))
                .isEqualByComparingTo(MemberEnsembleStatus.FULL);
    }

    @Test
    void declinedMemberAndFutureEnsembleAndHasSpaceThenStatusDeclined() throws Exception {
        Ensemble futureEnsemble = EnsembleFactory.withStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberId.of(31);
        futureEnsemble.declinedBy(memberId);

        assertThat(futureEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                .isEqualByComparingTo(MemberEnsembleStatus.DECLINED);
    }

    @Test
    void declinedMemberAndFutureEnsembleIsFullThenStatusDeclinedFull() throws Exception {
        Ensemble futureFullEnsemble = EnsembleFactory.ensembleAtCapacityWithStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberId.of(31);
        futureFullEnsemble.declinedBy(memberId);

        assertThat(futureFullEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                .isEqualByComparingTo(MemberEnsembleStatus.DECLINED_FULL);
    }

    @Test
    void acceptedMemberAndPastUncompletedEnsembleThenStatusPendingCompleted() throws Exception {
        Ensemble pastEnsemble = EnsembleFactory.withStartTime(2021, 11, 21, 11);
        MemberId memberId = MemberId.of(41);
        pastEnsemble.acceptedBy(memberId);

        assertThat(pastEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                .isEqualByComparingTo(MemberEnsembleStatus.PENDING_COMPLETED);
    }

    @Test
    void acceptedMemberAndCompletedEnsembleThenStatusCompleted() throws Exception {
        Ensemble completedEnsemble = EnsembleFactory.withStartTime(2021, 11, 21, 11);
        MemberId memberId = MemberId.of(41);
        completedEnsemble.acceptedBy(memberId);
        completedEnsemble.complete();

        assertThat(completedEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                .isEqualByComparingTo(MemberEnsembleStatus.COMPLETED);
    }

    @Test
    void acceptedMemberAndFutureEnsembleThenStatusAccepted() throws Exception {
        Ensemble futureEnsemble = EnsembleFactory.withStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberId.of(41);
        futureEnsemble.acceptedBy(memberId);

        assertThat(futureEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                .isEqualByComparingTo(MemberEnsembleStatus.ACCEPTED);
    }

    //   Member = Accepted,  -> IN_PROGRESS: only show Zoom link
    @Test
    void acceptedMemberAndInProgressEnsembleThenStatusInGracePeriod() throws Exception {
        ZonedDateTime startDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 11);
        Ensemble inGracePeriodEnsemble = EnsembleFactory.withStartTime(startDateTime);
        MemberId memberId = MemberId.of(41);
        inGracePeriodEnsemble.acceptedBy(memberId);

        ZonedDateTime currentDateTime = startDateTime.plusMinutes(10).minusSeconds(1); // grace period is 10 minutes
        assertThat(inGracePeriodEnsemble.statusFor(memberId, currentDateTime))
                .isEqualByComparingTo(MemberEnsembleStatus.IN_GRACE_PERIOD);
    }

    @Test
    void acceptedMemberAndCanceledThenStatusIsCanceled() throws Exception {
        Member member = new Member("first", "username");
        member.setId(MemberId.of(11));
        Ensemble ensemble = new EnsembleBuilderAndSaviour()
                .accept(member)
                .asCanceled()
                .build();
        MemberId memberId = MemberId.of(11);

        MemberEnsembleStatus memberEnsembleStatus = ensemble.statusFor(memberId, ZonedDateTime.now());

        assertThat(memberEnsembleStatus)
                .isEqualByComparingTo(MemberEnsembleStatus.CANCELED);
    }

    @Test
    void unknownMemberEnsembleInFutureWhenCanceledThenStatusIsHidden() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTime(2022, 1, 3, 9);
        ensemble.cancel();
        MemberId memberId = MemberId.of(99);

        MemberEnsembleStatus memberEnsembleStatus = ensemble.statusFor(memberId, UTC_2021_11_22_12);

        assertThat(memberEnsembleStatus)
                .isEqualByComparingTo(MemberEnsembleStatus.HIDDEN);
    }

    @Test
    void declinedMemberEnsembleInFutureWhenCanceledThenStatusIsHidden() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberId.of(41);
        ensemble.declinedBy(memberId);
        ensemble.cancel();

        MemberEnsembleStatus memberEnsembleStatus = ensemble.statusFor(memberId, UTC_2021_11_22_12);

        assertThat(memberEnsembleStatus)
                .isEqualByComparingTo(MemberEnsembleStatus.HIDDEN);
    }

    @Test
    void acceptedMemberAndLaterThanGracePeriodAndNotCompletedThenStatusIsHidden() throws Exception {
        ZonedDateTime startDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2022, 2, 3, 16);
        Ensemble inGracePeriodEnsemble = EnsembleFactory.withStartTime(startDateTime);
        MemberId memberId = MemberId.of(41);
        inGracePeriodEnsemble.acceptedBy(memberId);

        ZonedDateTime currentDateTime = startDateTime.plusMinutes(10).plusSeconds(1); // grace period is 10 minutes
        assertThat(inGracePeriodEnsemble.statusFor(memberId, currentDateTime))
                .isEqualByComparingTo(MemberEnsembleStatus.HIDDEN);
    }

    @Test
    void unknownOrDeclinedWhenEnsembleStartedStatusIsHidden() throws Exception {
        ZonedDateTime startDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2022, 2, 3, 16);
        Ensemble alreadyStartedEnsemble = EnsembleFactory.withStartTime(startDateTime);
        MemberId memberId = MemberId.of(41);
        ZonedDateTime currentDateTime = startDateTime.plusMinutes(1);

        assertThat(alreadyStartedEnsemble.statusFor(memberId, currentDateTime))
                .isEqualByComparingTo(MemberEnsembleStatus.HIDDEN);

        alreadyStartedEnsemble.declinedBy(memberId);
        assertThat(alreadyStartedEnsemble.statusFor(memberId, currentDateTime))
                .isEqualByComparingTo(MemberEnsembleStatus.HIDDEN);
    }

    // ---- [start] ----- [end of grace period] --------------- [end of start + duration] ---
    //              IN_PROG                       HIDDEN                                     COMPLETED

    // Ensemble has started (start time + 10 min):
    //   Member = Unknown  -> (same as in the past) HIDDEN
    //   Member = Declined -> HIDDEN
    // Ensemble has gone beyond grace period (start time + 11 min)
    //   Member = (doesn't matter) -> HIDDEN


}