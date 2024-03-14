package com.jitterted.mobreg.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class RotationTest {

    @Test
    void rolesAssignedAccordingToOrderOfParticipantsWhenCreated() {
        RotationFixture fixture = createFiveParticipantRotation();

        assertThat(fixture.rotation().nextDriver())
                .isEqualTo(fixture.nextDriver());
        assertThat(fixture.rotation().driver())
                .isEqualTo(fixture.driver());
        assertThat(fixture.rotation().navigator())
                .isEqualTo(fixture.navigator());
        assertThat(fixture.rotation().restOfParticipants())
                .containsExactly(fixture.participant1(), fixture.participant2());

        // above is exactly the same as this:
        assertThat(fixture.rotation().restOfParticipants())
                .containsExactlyElementsOf(List.of(fixture.participant1(), fixture.participant2()));
    }

    @Test
    void whenEnsembleSizeOfThreeRestOfParticipantsReturnsEmptyList() {
        Member nextDriver = MemberFactory.createMember(1, "One", "irrelevant");
        Member driver = MemberFactory.createMember(2, "Two", "irrelevant");
        Member navigator = MemberFactory.createMember(3, "Three", "irrelevant");

        Rotation rotation = new Rotation(List.of(nextDriver,
                                                 driver,
                                                 navigator));

        assertThat(rotation.restOfParticipants())
                .isEmpty();
    }

    @Test
    void whenEnsembleSizeOfFourRestOfParticipantsReturnsOneParticipant() {
        Member nextDriver = MemberFactory.createMember(1, "One", "irrelevant");
        Member driver = MemberFactory.createMember(2, "Two", "irrelevant");
        Member navigator = MemberFactory.createMember(3, "Three", "irrelevant");
        Member participant = MemberFactory.createMember(4, "Four", "irrelevant");

        Rotation rotation = new Rotation(List.of(nextDriver,
                                                 driver,
                                                 navigator,
                                                 participant));

        assertThat(rotation.restOfParticipants())
                .containsExactly(participant);
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
                                   Next Driver: Member: id=MemberId=1, firstName='One', githubUsername='irrelevant', roles=[], timeZone=Z
                                   Driver: Member: id=MemberId=2, firstName='Two', githubUsername='irrelevant', roles=[], timeZone=Z
                                   Navigator: Member: id=MemberId=3, firstName='Three', githubUsername='irrelevant', roles=[], timeZone=Z
                                   Rest of participants: [Member: id=MemberId=4, firstName='Four', githubUsername='irrelevant', roles=[], timeZone=Z, Member: id=MemberId=5, firstName='Five', githubUsername='irrelevant', roles=[], timeZone=Z]""");

        rotation.rotate();

        assertThat(rotation.driver())
                .isEqualTo(fixture.nextDriver());
        assertThat(rotation.navigator())
                .isEqualTo(fixture.driver());
        assertThat(rotation.nextDriver())
                .isEqualTo(fixture.participant2());
        assertThat(rotation.restOfParticipants())
                .containsExactly(fixture.navigator(), fixture.participant1());

        assertThat(rotation.toString())
                .isEqualTo("""
                                  Next Driver: Member: id=MemberId=5, firstName='Five', githubUsername='irrelevant', roles=[], timeZone=Z
                                  Driver: Member: id=MemberId=1, firstName='One', githubUsername='irrelevant', roles=[], timeZone=Z
                                  Navigator: Member: id=MemberId=2, firstName='Two', githubUsername='irrelevant', roles=[], timeZone=Z
                                  Rest of participants: [Member: id=MemberId=3, firstName='Three', githubUsername='irrelevant', roles=[], timeZone=Z, Member: id=MemberId=4, firstName='Four', githubUsername='irrelevant', roles=[], timeZone=Z]""");
        rotation.rotate();
        assertThat(rotation.toString())
                .isEqualTo("""
                                   Next Driver: Member: id=MemberId=4, firstName='Four', githubUsername='irrelevant', roles=[], timeZone=Z
                                   Driver: Member: id=MemberId=5, firstName='Five', githubUsername='irrelevant', roles=[], timeZone=Z
                                   Navigator: Member: id=MemberId=1, firstName='One', githubUsername='irrelevant', roles=[], timeZone=Z
                                   Rest of participants: [Member: id=MemberId=2, firstName='Two', githubUsername='irrelevant', roles=[], timeZone=Z, Member: id=MemberId=3, firstName='Three', githubUsername='irrelevant', roles=[], timeZone=Z]""");
    }

    private static Rotation createRotationWithTwoParticipants() {
        return new Rotation(List.of(MemberFactory.createMember(1, "One", "irrelevant"),
                                    MemberFactory.createMember(2, "Two", "irrelevant")));
    }

    private RotationFixture createFiveParticipantRotation() {
        Member nextDriver = MemberFactory.createMember(1, "One", "irrelevant");
        Member driver = MemberFactory.createMember(2, "Two", "irrelevant");
        Member navigator = MemberFactory.createMember(3, "Three", "irrelevant");
        Member participant1 = MemberFactory.createMember(4, "Four", "irrelevant");
        Member participant2 = MemberFactory.createMember(5, "Five", "irrelevant");

        Rotation rotation = new Rotation(List.of(nextDriver,
                                                 driver,
                                                 navigator,
                                                 participant1,
                                                 participant2));
        return new RotationFixture(nextDriver, driver, navigator, participant1, participant2, rotation);
    }

    private record RotationFixture(Member nextDriver,
                                   Member driver,
                                   Member navigator,
                                   Member participant1,
                                   Member participant2,
                                   Rotation rotation) {
    }
}