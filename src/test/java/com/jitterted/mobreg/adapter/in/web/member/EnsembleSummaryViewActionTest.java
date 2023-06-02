package com.jitterted.mobreg.adapter.in.web.member;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EnsembleSummaryViewActionTest {

    @Nested
    class SpectatorActionIs {

        @Test
        void joinAsSpectatorWhenMemberIsUnknown() {
            fail("start here");
        }

        @Test
        void whenMemberIsParticipantThenSpectatorActionIsBecomeSpectator() {
        }

        @Test
        void whenMemberIsDeclinedThenSpectatorActionIsJoinAsSpectator() {
        }

    }

    @Nested
    class ParticipantActionIs {

        @Test
        void becomeSpectatorWhenMemberIsUnknown() {
        }
    }

}