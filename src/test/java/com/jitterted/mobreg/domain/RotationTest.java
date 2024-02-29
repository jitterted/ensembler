package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class RotationTest {

    @Test
    void rolesAssignedAccordingToOrderOfParticipantsWhenCreated() {
        MemberId driverId = MemberId.of(3L);
        MemberId navigatorId = MemberId.of(7L);
        MemberId nextDriverId = MemberId.of(2L);
        MemberId participantId1 = MemberId.of(1L);
        MemberId participantId2 = MemberId.of(9L);

        Rotation rotation = new Rotation(List.of(driverId,
                                                 navigatorId,
                                                 nextDriverId,
                                                 participantId1,
                                                 participantId2));

        assertThat(rotation.driver())
                .isEqualTo(driverId);
        assertThat(rotation.navigator())
                .isEqualTo(navigatorId);
        assertThat(rotation.nextDriver())
                .isEqualTo(nextDriverId);
        assertThat(rotation.restOfParticipants())
                .containsExactly(participantId1, participantId2);

        // above is exactly the same as this:
        assertThat(rotation.restOfParticipants())
                .containsExactlyElementsOf(List.of(participantId1, participantId2));
    }

    void rotationThrowsExceptionIfFewerThan3Participants() {
//        assertThatExceptionOfType(NotEnoughParticipants)
//                .isThrownBy(() -> new Rotation(List.of(MemberId.of(1L), MemberId.of(67L))))
//                .withMessage("")
    }
}