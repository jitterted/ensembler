package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.application.EnsembleBuilderAndSaviour;
import com.jitterted.mobreg.application.TestMemberBuilder;
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
    //   Member = Accepted,  -> IN_PROGRESS: only show Zoom link
    public void acceptedMemberAndInProgressEnsembleThenStatusInGracePeriod() throws Exception {
        ZonedDateTime startDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 11);
        Ensemble inGracePeriodEnsemble = EnsembleFactory.withStartTime(startDateTime);
        MemberId memberId = MemberId.of(41);
        inGracePeriodEnsemble.acceptedBy(memberId);

        ZonedDateTime currentDateTime = startDateTime.plusMinutes(10).minusSeconds(1); // grace period is 10 minutes
        assertThat(inGracePeriodEnsemble.statusFor(memberId, currentDateTime))
                .isEqualByComparingTo(MemberStatus.IN_GRACE_PERIOD);
    }

    @Test
    public void acceptedMemberAndCanceledThenStatusIsCanceled() throws Exception {
        Ensemble ensemble = new EnsembleBuilderAndSaviour()
                .accept(new TestMemberBuilder())
                .asCanceled()
                .build();
        MemberId memberId = ensemble.acceptedMembers().findFirst().get();

        MemberStatus memberStatus = ensemble.statusFor(memberId, ZonedDateTime.now());

        assertThat(memberStatus)
                .isEqualByComparingTo(MemberStatus.CANCELED);
    }

    @Test
    public void unknownMemberEnsembleInFutureWhenCanceledThenStatusIsHidden() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTime(2022, 1, 3, 9);
        ensemble.cancel();
        MemberId memberId = MemberId.of(99);

        MemberStatus memberStatus = ensemble.statusFor(memberId, UTC_2021_11_22_12);

        assertThat(memberStatus)
                .isEqualByComparingTo(MemberStatus.HIDDEN);
    }

    @Test
    public void declinedMemberEnsembleInFutureWhenCanceledThenStatusIsHidden() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTime(2022, 1, 3, 9);
        MemberId memberId = MemberId.of(41);
        ensemble.declinedBy(memberId);
        ensemble.cancel();

        MemberStatus memberStatus = ensemble.statusFor(memberId, UTC_2021_11_22_12);

        assertThat(memberStatus)
                .isEqualByComparingTo(MemberStatus.HIDDEN);
    }

    @Test
    public void acceptedMemberAndLaterThanGracePeriodAndNotCompletedThenStatusIsHidden() throws Exception {
        ZonedDateTime startDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2022, 2, 3, 16);
        Ensemble inGracePeriodEnsemble = EnsembleFactory.withStartTime(startDateTime);
        MemberId memberId = MemberId.of(41);
        inGracePeriodEnsemble.acceptedBy(memberId);

        ZonedDateTime currentDateTime = startDateTime.plusMinutes(10).plusSeconds(1); // grace period is 10 minutes
        assertThat(inGracePeriodEnsemble.statusFor(memberId, currentDateTime))
                .isEqualByComparingTo(MemberStatus.HIDDEN);
    }
    
    @Test
    public void unknownOrDeclinedWhenEnsembleStartedStatusIsHidden() throws Exception {
        ZonedDateTime startDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2022, 2, 3, 16);
        Ensemble alreadyStartedEnsemble = EnsembleFactory.withStartTime(startDateTime);
        MemberId memberId = MemberId.of(41);
        ZonedDateTime currentDateTime = startDateTime.plusMinutes(1);

        assertThat(alreadyStartedEnsemble.statusFor(memberId, currentDateTime))
                .isEqualByComparingTo(MemberStatus.HIDDEN);

        alreadyStartedEnsemble.declinedBy(memberId);
        assertThat(alreadyStartedEnsemble.statusFor(memberId, currentDateTime))
                .isEqualByComparingTo(MemberStatus.HIDDEN);
    }
    
    // ---- [start] ----- [end of grace period] --------------- [end of start + duration] ---
    //              IN_PROG                       HIDDEN                                     COMPLETED

    // Ensemble has started (start time + 10 min):
    //   Member = Unknown  -> (same as in the past) HIDDEN
    //   Member = Declined -> HIDDEN
    // Ensemble has gone beyond grace period (start time + 11 min)
    //   Member = (doesn't matter) -> HIDDEN


}