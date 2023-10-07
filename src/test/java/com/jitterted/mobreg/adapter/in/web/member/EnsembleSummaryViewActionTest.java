package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.domain.MemberStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EnsembleSummaryViewActionTest {

    @Nested
    class SpectatorActionIs {

        @Test
        void joinAsSpectatorWhenMemberIsUnknown() {
            SpectatorAction spectatorAction = SpectatorAction.from(MemberStatus.UNKNOWN);

            SpectatorAction expectedAction = new SpectatorAction(
                    "/member/join-as-spectator",
                    "Join as Spectator &#x1F440;");

            assertThat(spectatorAction)
                    .isEqualTo(expectedAction);
        }

        @Test
        void switchToSpectatorWhenMemberIsParticipant() {
            SpectatorAction spectatorAction = SpectatorAction.from(MemberStatus.PARTICIPANT);

            SpectatorAction expectedAction = new SpectatorAction(
                    "/member/join-as-spectator",
                    "Switch to Spectator &#x1F440;");

            assertThat(spectatorAction)
                    .isEqualTo(expectedAction);

        }

        @Test
        void joinAsSpectatorWhenMemberIsDeclined() {
            SpectatorAction spectatorAction = SpectatorAction.from(MemberStatus.DECLINED);

            SpectatorAction expectedAction = new SpectatorAction(
                    "/member/join-as-spectator",
                    "Join as Spectator &#x1F440;");

            assertThat(spectatorAction)
                    .isEqualTo(expectedAction);
        }

        @Test
        void leaveWhenMemberIsSpectator() {
            SpectatorAction spectatorAction = SpectatorAction.from(MemberStatus.SPECTATOR);

            SpectatorAction expectedAction = new SpectatorAction(
                    "/member/decline",
                    "Leave Spectators &#x1f44b;");

            assertThat(spectatorAction)
                    .isEqualTo(expectedAction);

        }
    }

    @Nested
    class ParticipantActionIs {

        @Test
        void participateInRotationWhenMemberIsUnknown() {
            ParticipantAction participantAction =
                    ParticipantAction.from(MemberStatus.UNKNOWN, false);

            ParticipantAction expectedParticipantAction =
                    new ParticipantAction(
                            "/member/accept",
                            "Participate in Rotation &#x2328;",
                            false);

            assertThat(participantAction)
                    .isEqualTo(expectedParticipantAction);
        }

        @Test
        void participateInRotationWhenMemberIsDeclined() {
            ParticipantAction participantAction =
                    ParticipantAction.from(MemberStatus.DECLINED, false);

            ParticipantAction expectedParticipantAction =
                    new ParticipantAction(
                            "/member/accept",
                            "Participate in Rotation &#x2328;",
                            false);

            assertThat(participantAction)
                    .isEqualTo(expectedParticipantAction);
        }
        
        @Test
        void leaveRotationWhenMemberIsParticipant() {
            ParticipantAction participantAction =
                    ParticipantAction.from(MemberStatus.PARTICIPANT, false);

            ParticipantAction expectedParticipantAction =
                    new ParticipantAction(
                            "/member/decline",
                            "Leave Rotation &#x1f44b;", // hand wave symbol
                            false);

            assertThat(participantAction)
                    .isEqualTo(expectedParticipantAction);

        }

        @Test
        void switchToParticipantWhenMemberIsSpectator() {
            ParticipantAction participantAction =
                    ParticipantAction.from(MemberStatus.SPECTATOR, false);

            ParticipantAction expectedParticipantAction =
                    new ParticipantAction(
                            "/member/accept",
                            "Switch to Participant &#x2328;", // keyboard symbol
                            false);

            assertThat(participantAction)
                    .isEqualTo(expectedParticipantAction);
        }

        @Test // parameterize for Unknown/Declined/Spectator
        @Disabled
        void disabledWhenMemberIsNotParticipantAndRotationIsFull() {
            ParticipantAction participantAction = ParticipantAction.from(MemberStatus.UNKNOWN, false);

            assertThat(participantAction.disabled())
                    .isTrue();
        }
    }

}