package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleMemberEnsembleStatusTest {
    private static final ZonedDateTime UTC_2021_11_22_12 = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 12);

    @Nested
    class unknownMember {
        @Test
        void pastEnsembleThenStatusHidden() throws Exception {
            ZonedDateTime startDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 11);
            Ensemble pastEnsemble = EnsembleFactory.withStartTime(startDateTime);
            MemberId memberId = MemberId.of(37);

            assertThat(pastEnsemble.statusFor(memberId, startDateTime.plusHours(3))) // duration defaults to 1h55m, so 3 hours means it's over
                                                                                     .isEqualByComparingTo(MemberEnsembleStatus.HIDDEN);
        }

        @Test
        void futureEnsembleAndHasSpaceThenStatusUnknown() throws Exception {
            Ensemble futureEnsemble = EnsembleFactory.withStartTime(2022, 1, 3, 9);
            MemberId memberId = MemberId.of(31);

            assertThat(futureEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                    .isEqualByComparingTo(MemberEnsembleStatus.UNKNOWN);
        }

        @Test
        void futureEnsembleAndIsFullThenStatusFull() throws Exception {
            Ensemble futureEnsemble = EnsembleFactory.ensembleAtCapacityWithStartTime(2022, 1, 3, 9);
            MemberId memberIdIsUnknown = MemberId.of(33);

            assertThat(futureEnsemble.statusFor(memberIdIsUnknown, UTC_2021_11_22_12))
                    .isEqualByComparingTo(MemberEnsembleStatus.FULL);
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
    }

    @Nested
    class declinedMember {

        @Test
        void ensembleInFutureWhenCanceledThenStatusIsHidden() throws Exception {
            Ensemble ensemble = EnsembleFactory.withStartTime(2022, 1, 3, 9);
            MemberId memberId = MemberId.of(41);
            ensemble.declinedBy(memberId);
            ensemble.cancel();

            MemberEnsembleStatus memberEnsembleStatus = ensemble.statusFor(memberId, UTC_2021_11_22_12);

            assertThat(memberEnsembleStatus)
                    .isEqualByComparingTo(MemberEnsembleStatus.HIDDEN);
        }

        @Test
        void andFutureEnsembleAndHasSpaceThenStatusDeclined() throws Exception {
            Ensemble futureEnsemble = EnsembleFactory.withStartTime(2022, 1, 3, 9);
            MemberId memberId = MemberId.of(31);
            futureEnsemble.declinedBy(memberId);

            assertThat(futureEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                    .isEqualByComparingTo(MemberEnsembleStatus.DECLINED);
        }

        @Test
        void andFutureEnsembleIsFullThenStatusDeclinedFull() throws Exception {
            Ensemble futureFullEnsemble = EnsembleFactory.ensembleAtCapacityWithStartTime(2022, 1, 3, 9);
            MemberId memberId = MemberId.of(31);
            futureFullEnsemble.declinedBy(memberId);

            assertThat(futureFullEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                    .isEqualByComparingTo(MemberEnsembleStatus.DECLINED_FULL);
        }
    }

    @Nested
    class acceptedMember {

        @ParameterizedTest
        @EnumSource(names = {"PARTICIPANT", "SPECTATOR"})
        void pastUncompletedEnsembleThenStatusPendingCompleted(MemberStatus memberStatus) throws Exception {
            Ensemble pastEnsemble = EnsembleFactory.withStartTime(2021, 11, 21, 11);
            MemberId memberId = MemberId.of(41);
            joinAs(memberStatus, pastEnsemble, memberId);

            assertThat(pastEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                    .isEqualByComparingTo(MemberEnsembleStatus.PENDING_COMPLETED);
        }

        @ParameterizedTest
        @EnumSource(names = {"PARTICIPANT", "SPECTATOR"})
        void completedEnsembleThenStatusCompleted(MemberStatus memberStatus) throws Exception {
            Ensemble completedEnsemble = EnsembleFactory.withStartTime(2021, 11, 21, 11);
            MemberId memberId = MemberId.of(41);
            joinAs(memberStatus, completedEnsemble, memberId);
            completedEnsemble.complete();

            assertThat(completedEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                    .isEqualByComparingTo(MemberEnsembleStatus.COMPLETED);
        }

        @ParameterizedTest
        @EnumSource(names = {"PARTICIPANT", "SPECTATOR"})
        void futureEnsembleThenStatusAccepted(MemberStatus memberStatus) throws Exception {
            Ensemble futureEnsemble = EnsembleFactory.withStartTime(2022, 1, 3, 9);
            MemberId memberId = MemberId.of(41);
            joinAs(memberStatus, futureEnsemble, memberId);

            assertThat(futureEnsemble.statusFor(memberId, UTC_2021_11_22_12))
                    .isEqualByComparingTo(MemberEnsembleStatus.ACCEPTED);
        }

        //   Member = Accepted,  -> IN_PROGRESS: only show Zoom link
        @ParameterizedTest
        @EnumSource(names = {"PARTICIPANT", "SPECTATOR"})
        void canceledThenStatusIsCanceled(MemberStatus memberStatus) throws Exception {
            Member member = new Member("first", "username");
            member.setId(MemberId.of(11));
            Ensemble ensemble = EnsembleFactory.withStartTimeNow();
            joinAs(memberStatus, ensemble, member.getId());
            ensemble.cancel();

            MemberEnsembleStatus memberEnsembleStatus = ensemble.statusFor(MemberId.of(11),
                                                                           ZonedDateTime.now());

            assertThat(memberEnsembleStatus)
                    .isEqualByComparingTo(MemberEnsembleStatus.CANCELED);
        }

        private static void joinAs(MemberStatus memberStatus, Ensemble ensemble, MemberId memberId) {
            switch (memberStatus) {
                case PARTICIPANT -> ensemble.joinAsParticipant(memberId);
                case SPECTATOR -> ensemble.joinAsSpectator(memberId);
            }
        }

        @Test
        void inProgressEnsembleThenStatusInGracePeriod() throws Exception {
            ZonedDateTime startDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 22, 11);
            Ensemble inGracePeriodEnsemble = EnsembleFactory.withStartTime(startDateTime);
            MemberId memberId = MemberId.of(41);
            joinAs(MemberStatus.PARTICIPANT, inGracePeriodEnsemble, memberId);

            ZonedDateTime currentDateTime = startDateTime.plusMinutes(10)
                                                         .minusSeconds(1); // grace period is 10 minutes
            assertThat(inGracePeriodEnsemble.statusFor(memberId, currentDateTime))
                    .isEqualByComparingTo(MemberEnsembleStatus.IN_GRACE_PERIOD);
        }

        @Test
        void laterThanGracePeriodAndNotCompletedThenStatusIsHidden() throws Exception {
            ZonedDateTime startDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2022, 2, 3, 16);
            Ensemble inGracePeriodEnsemble = EnsembleFactory.withStartTime(startDateTime);
            MemberId memberId = MemberId.of(41);
            joinAs(MemberStatus.PARTICIPANT, inGracePeriodEnsemble, memberId);

            ZonedDateTime currentDateTime = startDateTime.plusMinutes(10)
                                                         .plusSeconds(1); // grace period is 10 minutes
            assertThat(inGracePeriodEnsemble.statusFor(memberId, currentDateTime))
                    .isEqualByComparingTo(MemberEnsembleStatus.HIDDEN);
        }

    }

    // ---- [start] ----- [end of grace period] --------------- [end of start + duration] ---
    //              IN_PROG                       HIDDEN                                     COMPLETED

    // Ensemble has started (start time + 10 min):
    //   Member = Unknown  -> (same as in the past) HIDDEN
    //   Member = Declined -> HIDDEN
    // Ensemble has gone beyond grace period (start time + 11 min)
    //   Member = (doesn't matter) -> HIDDEN


}