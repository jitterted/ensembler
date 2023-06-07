package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.domain.MemberStatus;
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
            ParticipantAction participantAction = ParticipantAction.from(MemberStatus.UNKNOWN);

            ParticipantAction expectedParticipantAction =
                    new ParticipantAction(
                            "/member/accept",
                            "Participate in Rotation &#x2328;"
                    );

            assertThat(participantAction)
                    .isEqualTo(expectedParticipantAction);
        }

        @Test
        void participateInRotationWhenMemberIsDeclined() {
            ParticipantAction participantAction = ParticipantAction.from(MemberStatus.DECLINED);

            ParticipantAction expectedParticipantAction =
                    new ParticipantAction(
                            "/member/accept",
                            "Participate in Rotation &#x2328;"
                    );

            assertThat(participantAction)
                    .isEqualTo(expectedParticipantAction);
        }
        
        @Test
        void leaveRotationWhenMemberIsParticipant() {
            ParticipantAction participantAction = ParticipantAction.from(MemberStatus.PARTICIPANT);

            ParticipantAction expectedParticipantAction =
                    new ParticipantAction(
                            "/member/decline",
                            "Leave Rotation &#x1f44b;"
                    );

            assertThat(participantAction)
                    .isEqualTo(expectedParticipantAction);

        }

        @Test
        void switchToParticipantWhenMemberIsSpectator() {
            ParticipantAction participantAction = ParticipantAction.from(MemberStatus.SPECTATOR);

            ParticipantAction expectedParticipantAction =
                    new ParticipantAction(
                            "/member/accept",
                            "Switch to Participant &#x1f44b;"
                    );

            assertThat(participantAction)
                    .isEqualTo(expectedParticipantAction);
        }

        @Test // parameterize
        void disabledWhenMemberIsNotParticipantAndRotationIsFull() {

        }
    }

}