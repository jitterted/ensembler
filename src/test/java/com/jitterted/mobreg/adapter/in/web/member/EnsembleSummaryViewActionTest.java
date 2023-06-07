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
        void becomeSpectatorWhenMemberIsUnknown() {
        }
    }

}