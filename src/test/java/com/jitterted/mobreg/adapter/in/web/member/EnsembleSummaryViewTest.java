package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.in.web.admin.MemberView;
import com.jitterted.mobreg.application.DefaultMemberService;
import com.jitterted.mobreg.application.GoogleCalendarLinkCreator;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.StubMemberService;
import com.jitterted.mobreg.application.TestMemberBuilder;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleBuilder;
import com.jitterted.mobreg.domain.EnsembleFactory;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class EnsembleSummaryViewTest {

    private static final StubMemberService STUB_MEMBER_SERVICE = new StubMemberService();
    private static final MemberId IRRELEVANT_MEMBER_ID = MemberId.of(42L);
    private static final int IRRELEVANT_ENSEMBLE_ID = 13;

    @Nested
    class AllViews {

        @Test
        void pastEnsemblesAreSortedDescendingByDate() {
            Ensemble ensembleYesterday = new EnsembleBuilder()
                    .scheduled(ZonedDateTime.now().minusDays(1))
                    .id(7).named("Yesterday").build();
            Ensemble ensembleLastWeek = new EnsembleBuilder()
                    .scheduled(ZonedDateTime.now().minusWeeks(1))
                    .id(3).named("Last Week").build();
            Ensemble ensembleLastMonth = new EnsembleBuilder()
                    .scheduled(ZonedDateTime.now().minusMonths(1))
                    .id(5).named("Last Month").build();
            List<Ensemble> ensembles = List.of(ensembleLastWeek, ensembleYesterday, ensembleLastMonth);
            MemberService memberService = new DefaultMemberService(new InMemoryMemberRepository());
            MemberId memberId = MemberId.of(71L);

            List<EnsembleSummaryView> viewsDescending = EnsembleSummaryView.from(ensembles, memberId, memberService, EnsembleSortOrder.DESCENDING_ORDER);

            assertThat(viewsDescending)
                    .extracting(EnsembleSummaryView::dateTime)
                    .as("Not sorted Descending")
                    .isSortedAccordingTo(Comparator.reverseOrder());
        }

        @Test
        void upcomingEnsemblesAreSortedAscendingByDate() {
            Ensemble ensembleTomorrow = new EnsembleBuilder()
                    .scheduled(ZonedDateTime.now().plusDays(1))
                    .id(7).named("Tomorrow").build();
            Ensemble ensembleNextWeek = new EnsembleBuilder()
                    .scheduled(ZonedDateTime.now().plusWeeks(1))
                    .id(3).named("Next Week").build();
            Ensemble ensembleNextMonth = new EnsembleBuilder()
                    .scheduled(ZonedDateTime.now().plusMonths(1))
                    .id(5).named("Next Month").build();
            List<Ensemble> ensembles = List.of(ensembleNextMonth, ensembleTomorrow, ensembleNextWeek);
            MemberService memberService = new DefaultMemberService(new InMemoryMemberRepository());
            MemberId memberId = MemberId.of(71L);

            List<EnsembleSummaryView> viewsAscending = EnsembleSummaryView.from(ensembles, memberId, memberService, EnsembleSortOrder.ASCENDING_ORDER);

            assertThat(viewsAscending)
                    .extracting(EnsembleSummaryView::dateTime)
                    .as("Not sorted Ascending")
                    .isSortedAccordingTo(Comparator.naturalOrder());
        }
    }

    @Test
    void memberNotRegisteredWhenEnsembleIsNotFullCanDoAnyAction() throws Exception {
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
    void joinedAsParticipantIsInParticipantList() throws Exception {
        FutureEnsembleMemberFixture fixture = createEnsemble1DayInFutureAndCreateMember();
        fixture.ensemble().joinAsParticipant(fixture.member().getId());

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView
                .toView(fixture.ensemble(), fixture.member().getId(), fixture.memberService());

        assertThat(ensembleSummaryView.participantCount())
                .isEqualTo(1);
        assertThat(ensembleSummaryView.participants())
                .containsExactly(MemberView.from(fixture.member()));
    }

    @Test
    void spectatorIsInSpectatorList() throws Exception {
        FutureEnsembleMemberFixture fixture = createEnsemble1DayInFutureAndCreateMember();
        fixture.ensemble().joinAsSpectator(fixture.member().getId());

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView
                .toView(fixture.ensemble(), fixture.member().getId(), fixture.memberService());

        assertThat(ensembleSummaryView.spectators())
                .containsExactly(MemberView.from(fixture.member()));
    }

    private static FutureEnsembleMemberFixture createEnsemble1DayInFutureAndCreateMember() {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        TestMemberBuilder memberBuilder = new TestMemberBuilder();
        Member member = memberBuilder.buildAndSave();
        MemberService memberService = memberBuilder.memberService();
        return new FutureEnsembleMemberFixture(ensemble, member, memberService);
    }

    private record FutureEnsembleMemberFixture(Ensemble ensemble, Member member, MemberService memberService) {
    }

    @Nested
    class StatusLinksOnly {

        @Test
        void forUnregisteredMemberAreEmpty() {
            Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();

            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(73L), STUB_MEMBER_SERVICE);

            assertThat(ensembleSummaryView.status().links())
                    .isEmpty();
            assertThat(ensembleSummaryView.status().messages())
                    .isEmpty();
        }

        @Test
        void forDeclinedMemberAreEmpty() {
            Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
            MemberId memberId = MemberId.of(73L);
            ensemble.declinedBy(memberId);

            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, memberId, STUB_MEMBER_SERVICE);

            assertThat(ensembleSummaryView.status().links())
                    .isEmpty();
            assertThat(ensembleSummaryView.status().messages())
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
            assertThat(ensembleSummaryView.status().links())
                    .containsExactly(calendarLink, zoomLink);
            assertThat(ensembleSummaryView.status().messages())
                    .isEmpty();
        }

        @Test
        void forSpectatorAreCalendarAndMeeting() {
            Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
            MemberId memberId = MemberId.of(97);
            ensemble.joinAsSpectator(memberId);

            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, memberId, STUB_MEMBER_SERVICE);

            assertThat(ensembleSummaryView.status().links())
                    .hasSize(2);
            assertThat(ensembleSummaryView.status().messages())
                    .isEmpty();
        }

        @Test
        void forCompletedEnsembleAndMemberParticipantIsRecording() throws Exception {
            Fixture fixture = createCompletedEnsembleWithRecordingLinkOf("https://recording.link/abc123");

            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(fixture.ensemble(),
                                                                                 fixture.memberId(),
                                                                                 STUB_MEMBER_SERVICE);

            assertThat(ensembleSummaryView.status().links())
                    .containsExactly(new DisplayLink("https://recording.link/abc123",
                                                     "Recording Link"));
            assertThat(ensembleSummaryView.status().messages())
                    .isEmpty();
        }

        Fixture createCompletedEnsembleWithRecordingLinkOf(String recordingUrl) {
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
    }

    @Nested
    class StatusMessageOnly {

        @Test
        void forPendingCompletedIsRecordingComingSoon() throws Exception {
            Ensemble ensemble = EnsembleFactory.withStartTime(ZonedDateTime.now().minusHours(2));
            ensemble.setId(EnsembleId.of(11));

            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble,
                                                                                 MemberId.of(1),
                                                                                 STUB_MEMBER_SERVICE);

            assertThat(ensembleSummaryView.status().messages())
                    .containsExactly("Recording Coming Soon...");
            assertThat(ensembleSummaryView.status().links())
                    .isEmpty();
        }

        @Test
        void forCanceledAndEndedInThePastHasLinkTextOfCanceled() {
            Ensemble canceledEnsemble = new EnsembleBuilder()
                    .endedInThePast()
                    .id(IRRELEVANT_ENSEMBLE_ID)
                    .asCanceled()
                    .build();

            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(canceledEnsemble,
                                                                                 IRRELEVANT_MEMBER_ID,
                                                                                 STUB_MEMBER_SERVICE);

            assertThat(ensembleSummaryView.status().messages())
                    .containsExactly("Ensemble Was Canceled");
            assertThat(ensembleSummaryView.status().links())
                    .isEmpty();
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

    @Nested
    class EnsembleFull {

        @Test
        void thenParticipateActionIsDisabled() throws Exception {
            Ensemble ensemble = EnsembleFactory.fullEnsembleOneDayInTheFuture();

            MemberId memberIdOfNonRegisteredMember = MemberId.of(99L);
            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, memberIdOfNonRegisteredMember, STUB_MEMBER_SERVICE);

            assertThat(ensembleSummaryView.participantAction().disabled())
                    .isTrue();
        }

        @Test
        void thenParticipatingMemberCanLeaveParticipants() {
            Ensemble ensemble = EnsembleFactory.fullEnsembleOneDayInTheFuture();
            MemberId memberId = ensemble.participants().findFirst().orElseThrow();

            EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, memberId, STUB_MEMBER_SERVICE);

            assertThat(ensembleSummaryView.participantAction().disabled())
                    .as("Participant Leave Action should be enabled")
                    .isFalse();
        }

    }

    @Test
    void ensembleWithNoOneParticipatingShowsNoParticipants() throws Exception {
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
