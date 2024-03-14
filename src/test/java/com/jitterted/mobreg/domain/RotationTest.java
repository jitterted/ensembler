package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class RotationTest {

    @Test
    void rolesAssignedAccordingToOrderOfParticipantsWhenCreated() {
        RotationFixture fixture = createFiveParticipantRotation();

        assertThat(fixture.rotation().nextDriver())
                .isEqualTo(fixture.nextDriverId());
        assertThat(fixture.rotation().driver())
                .isEqualTo(fixture.driverId());
        assertThat(fixture.rotation().navigator())
                .isEqualTo(fixture.navigatorId());
        assertThat(fixture.rotation().restOfParticipants())
                .containsExactly(fixture.participantId1(), fixture.participantId2());

        // above is exactly the same as this:
        assertThat(fixture.rotation().restOfParticipants())
                .containsExactlyElementsOf(List.of(fixture.participantId1(), fixture.participantId2()));
    }

    @Test
    void whenEnsembleSizeOfThreeRestOfParticipantsReturnsEmptyList() {
        MemberId nextDriverId = MemberId.of(1L);
        MemberId driverId = MemberId.of(2L);
        MemberId navigatorId = MemberId.of(3L);

        Rotation rotation = new Rotation(List.of(nextDriverId,
                                                 driverId,
                                                 navigatorId));

        assertThat(rotation.restOfParticipants())
                .isEmpty();
    }

    @Test
    void whenEnsembleSizeOfFourRestOfParticipantsReturnsOneParticipant() {
        MemberId nextDriverId = MemberId.of(1L);
        MemberId driverId = MemberId.of(2L);
        MemberId navigatorId = MemberId.of(3L);
        MemberId participantId = MemberId.of(4L);

        Rotation rotation = new Rotation(List.of(nextDriverId,
                                                 driverId,
                                                 navigatorId,
                                                 participantId));

        assertThat(rotation.restOfParticipants())
                .containsExactly(participantId);
    }

    @Test
    void rotationThrowsExceptionIfFewerThan3Participants() {
        assertThatExceptionOfType(NotEnoughParticipants.class)
                .isThrownBy(RotationTest::createRotationWithTwoParticipants)
                .withMessage("2 is too few participants, requires minimum of 3 participants.");
    }

    @Test
    void rotateSwapsAllRolesToNextInLine() {
        RotationFixture fixture = createFiveParticipantRotation();
        Rotation rotation = fixture.rotation();
        assertThat(rotation.toString())
                .isEqualTo("""
                                Next Driver: MemberId=1
                                Driver: MemberId=2
                                Navigator: MemberId=3
                                Rest of participants: [MemberId=4, MemberId=5]""");

        rotation.rotate();

        assertThat(rotation.driver())
                .isEqualTo(fixture.nextDriverId());
        assertThat(rotation.navigator())
                .isEqualTo(fixture.driverId());
        assertThat(rotation.nextDriver())
                .isEqualTo(fixture.participantId2());
        assertThat(rotation.restOfParticipants())
                .containsExactly(fixture.navigatorId(), fixture.participantId1());

        assertThat(rotation.toString())
                .isEqualTo("""
                                   Next Driver: MemberId=5
                                   Driver: MemberId=1
                                   Navigator: MemberId=2
                                   Rest of participants: [MemberId=3, MemberId=4]""");
        rotation.rotate();
        assertThat(rotation.toString())
                .isEqualTo("""
                                   Next Driver: MemberId=4
                                   Driver: MemberId=5
                                   Navigator: MemberId=1
                                   Rest of participants: [MemberId=2, MemberId=3]""");
    }

    private static Rotation createRotationWithTwoParticipants() {
        return new Rotation(List.of(MemberId.of(1L), MemberId.of(67L)));
    }

    private RotationFixture createFiveParticipantRotation() {
        MemberId nextDriverId = MemberId.of(1L);
        MemberId driverId = MemberId.of(2L);
        MemberId navigatorId = MemberId.of(3L);
        MemberId participantId1 = MemberId.of(4L);
        MemberId participantId2 = MemberId.of(5L);

        Rotation rotation = new Rotation(List.of(nextDriverId,
                                                 driverId,
                                                 navigatorId,
                                                 participantId1,
                                                 participantId2));
        return new RotationFixture(nextDriverId, driverId, navigatorId, participantId1, participantId2, rotation);
    }

    private record RotationFixture(MemberId nextDriverId,
                                   MemberId driverId,
                                   MemberId navigatorId,
                                   MemberId participantId1,
                                   MemberId participantId2,
                                   Rotation rotation) {
    }
}