package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.in.web.admin.MemberView;
import com.jitterted.mobreg.application.DefaultMemberService;
import com.jitterted.mobreg.application.GoogleCalendarLinkCreator;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.StubMemberService;
import com.jitterted.mobreg.application.TestMemberBuilder;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleFactory;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleSummaryViewTest {

    private static final StubMemberService STUB_MEMBER_SERVICE = new StubMemberService();

    @Test
    void memberStatusUnknownWhenEnsembleIsEmptyCanDoAnyAction() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        MemberService memberService = new DefaultMemberService(new InMemoryMemberRepository());
        MemberId memberId = MemberId.of(97L);

        EnsembleSummaryView ensembleSummaryView =
                EnsembleSummaryView.toView(ensemble, memberId, memberService);

        MemberStatus memberStatus = ensemble.memberStatusFor(memberId);
        assertThat(ensembleSummaryView.spectatorAction())
                .isEqualTo(SpectatorAction.from(memberStatus));
        assertThat(ensembleSummaryView.participantAction())
                .isEqualTo(ParticipantAction.from(memberStatus, false));
    }

    @Test
    void withAnotherAcceptedMemberThenMemberAcceptedIsFalse() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        TestMemberBuilder memberBuilder = new TestMemberBuilder();
        Member member = memberBuilder
                .withFirstName("name")
                .withGithubUsername("seven")
                .buildAndSave();
        ensemble.joinAsParticipant(member.getId());

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView
                .toView(ensemble, MemberId.of(99L), memberBuilder.memberService());

        assertThat(ensembleSummaryView.participantCount())
                .isEqualTo(1);

        assertThat(ensembleSummaryView.memberStatus())
                .isEqualTo("unknown");
    }

    @Test
    void memberAcceptedIsTrueWhenMemberEnsembleParticipant() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        TestMemberBuilder memberBuilder = new TestMemberBuilder();
        Member member = memberBuilder
                .withFirstName("name")
                .withGithubUsername("participant_username")
                .buildAndSave();
        ensemble.joinAsParticipant(member.getId());

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView
                .toView(ensemble, member.getId(), memberBuilder.memberService());

        assertThat(ensembleSummaryView.memberStatus())
                .isEqualTo("accepted");
    }

    @Test
    void spectatorIsInSpectatorList() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        TestMemberBuilder memberBuilder = new TestMemberBuilder();
        Member member = memberBuilder
                .withFirstName("name")
                .withGithubUsername("participant_username")
                .buildAndSave();
        ensemble.joinAsSpectator(member.getId());

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView
                .toView(ensemble, member.getId(), memberBuilder.memberService());

        assertThat(ensembleSummaryView.spectators())
                .containsExactly(MemberView.from(member));
    }

    @Nested
    class Links {

        @Test
        void forUnknownMemberAreEmpty() {
            Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();

            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(73L), STUB_MEMBER_SERVICE);

            assertThat(ensembleSummaryView.links())
                    .isEmpty();
        }

        @Test
        void forDeclinedMemberAreEmpty() {
            Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
            MemberId memberId = MemberId.of(73L);
            ensemble.declinedBy(memberId);

            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, memberId, STUB_MEMBER_SERVICE);

            assertThat(ensembleSummaryView.links())
                    .isEmpty();
        }

        @Test
        void forParticipantAreCalendarAndMeeting() throws Exception {
            Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
            ensemble.changeMeetingLinkTo(URI.create("https://zoom.us/test"));
            MemberId memberId = MemberId.of(42);
            ensemble.joinAsParticipant(memberId);

            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, memberId, STUB_MEMBER_SERVICE);

            DisplayLink calendarLink = new DisplayLink(
                    new GoogleCalendarLinkCreator().createFor(ensemble),
                    "<i class=\"fas fa-calendar-plus pr-2\" aria-hidden=\"true\"></i>Add to Google Calendar");
            DisplayLink zoomLink = new DisplayLink(
                    "https://zoom.us/test",
                    "<i class=\"far fa-video pr-2\" aria-hidden=\"true\"></i>Zoom Link");
            assertThat(ensembleSummaryView.links())
                    .containsExactly(calendarLink, zoomLink);
        }

        @Test
        void forSpectatorAreCalendarAndMeeting() {
            Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
            MemberId memberId = MemberId.of(97);
            ensemble.joinAsSpectator(memberId);

            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, memberId, STUB_MEMBER_SERVICE);

            assertThat(ensembleSummaryView.links())
                    .hasSize(2);
        }

        @Test
        void forCompletedEnsembleAndMemberParticipantIsRecording() throws Exception {
            Fixture fixture = completedEnsembleWithRecordingLinkOf("https://recording.link/abc123");

            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(fixture.ensemble(),
                                                                                 fixture.memberId(),
                                                                                 STUB_MEMBER_SERVICE);

            assertThat(ensembleSummaryView.links())
                    .containsExactly(new DisplayLink("https://recording.link/abc123",
                                                     "Recording Link"));
        }

        Fixture completedEnsembleWithRecordingLinkOf(String recordingUrl) {
            Ensemble ensemble = new Ensemble("test", ZonedDateTime.now().minusDays(1));
            ensemble.setId(EnsembleId.of(1L));
            MemberId memberId = MemberId.of(11);
            ensemble.joinAsParticipant(memberId);
            ensemble.complete();
            ensemble.linkToRecordingAt(URI.create(recordingUrl));
            return new Fixture(ensemble, memberId);
        }

        private record Fixture(Ensemble ensemble, MemberId memberId) {
        }

        @Test
        void forPendingCompletedIsRecordingComingSoon() throws Exception {
            Ensemble ensemble = EnsembleFactory.withStartTime(ZonedDateTime.now().minusHours(2));
            ensemble.setId(EnsembleId.of(11));

            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(1), STUB_MEMBER_SERVICE);

            assertThat(ensembleSummaryView.links())
                    .containsExactly(new DisplayLink("", "Recording Coming Soon..."));
        }
    }

    @Nested
    class ActionButtonsAre {

        @Test
        void visibleWhenEnsembleIsInTheFuture() {
            Ensemble ensemble = EnsembleFactory.oneDayInTheFuture();

            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble,
                                                                                 MemberId.of(37L),
                                                                                 STUB_MEMBER_SERVICE);

            assertThat(ensembleSummaryView.showActionButtons())
                    .isTrue();
        }

        @Test
        void hiddenWhenEnsembleIsInThePast() {
            Ensemble ensemble = EnsembleFactory.oneDayInThePast();

            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble,
                                                                                 MemberId.of(23L),
                                                                                 STUB_MEMBER_SERVICE);

            assertThat(ensembleSummaryView.showActionButtons())
                    .isFalse();
        }

    }


    @Test
    void unknownMemberWhenEnsembleIsFullThenParticipateIsDisabled() throws Exception {
        Ensemble ensemble = EnsembleFactory.fullEnsembleOneDayInTheFuture();

        MemberId memberIdOfUnknownMember = MemberId.of(99L);
        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, memberIdOfUnknownMember, STUB_MEMBER_SERVICE);

        assertThat(ensembleSummaryView.memberStatus())
                .isEqualTo("full");
        assertThat(ensembleSummaryView.participantAction().disabled())
                .isTrue();
    }

    @Test
    void participatingMemberWhenEnsembleIsFullThenLeaveParticipantsIsEnabled() {
        Ensemble ensemble = EnsembleFactory.fullEnsembleOneDayInTheFuture();
        MemberId memberId = ensemble.acceptedMembers().findFirst().orElseThrow();

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, memberId, STUB_MEMBER_SERVICE);

        assertThat(ensembleSummaryView.participantAction())
                .isEqualTo(ParticipantAction.from(MemberStatus.PARTICIPANT, false));
    }

    @Test
    void viewIndicatesCanAcceptIfEnsembleIsNotFull() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        EnsembleFactory.acceptCountMembersFor(2, ensemble);

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(1), STUB_MEMBER_SERVICE);

        assertThat(ensembleSummaryView.memberStatus())
                .isEqualTo("accepted");
    }

    @Test
    void ensembleWithNoOneAcceptedShowsNoAcceptedMembers() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        MemberService memberService = new DefaultMemberService(new InMemoryMemberRepository());

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView
                .toView(ensemble, MemberId.of(11L), memberService);

        assertThat(ensembleSummaryView.participants())
                .isEmpty();
    }

    @Test
    void ensembleWithAcceptedMembersGetsInfoOnAndShowsWhoAccepted() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        TestMemberBuilder memberBuilder = new TestMemberBuilder();
        MemberService memberService = memberBuilder.memberService();
        Member member1 = memberBuilder
                .withFirstName("one")
                .withGithubUsername("github_one")
                .buildAndSave();
        Member member2 = memberBuilder
                .withFirstName("two")
                .withGithubUsername("github_two")
                .buildAndSave();
        ensemble.joinAsParticipant(member1.getId());
        ensemble.joinAsParticipant(member2.getId());

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView
                .toView(ensemble, MemberId.of(member1.getId().id()), memberService);

        assertThat(ensembleSummaryView.participants())
                .extracting(MemberView::firstName)
                .containsExactlyInAnyOrder("one", "two");
        assertThat(ensembleSummaryView.participants())
                .extracting(MemberView::githubUsername)
                .containsExactlyInAnyOrder("github_one", "github_two");
    }
}
