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
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberStatus;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.*;

class EnsembleSummaryViewTest {

    @Test
    public void memberStatusUnknownAndActionsExistWhenEnsembleIsEmpty() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        MemberService memberService = new DefaultMemberService(new InMemoryMemberRepository());
        MemberId memberId = MemberId.of(97L);

        EnsembleSummaryView ensembleSummaryView =
            EnsembleSummaryView.toView(ensemble, memberId, memberService);

        // the .memberStatus will go away once we're done replacing it with Actions
        assertThat(ensembleSummaryView.memberStatus())
            .isEqualTo("unknown");
        MemberStatus memberStatus = ensemble.memberStatusFor(memberId);
        assertThat(ensembleSummaryView.spectatorAction())
                .isEqualTo(SpectatorAction.from(memberStatus));
        assertThat(ensembleSummaryView.participantAction())
                .isEqualTo(ParticipantAction.from(memberStatus));
    }

    @Test
    public void withAnotherAcceptedMemberThenMemberAcceptedIsFalse() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        TestMemberBuilder memberBuilder = new TestMemberBuilder();
        Member member = memberBuilder
                .withFirstName("name")
                .withGithubUsername("seven")
                .buildAndSave();
        ensemble.acceptedBy(member.getId());

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView
            .toView(ensemble, MemberId.of(99L), memberBuilder.memberService());

        assertThat(ensembleSummaryView.participantCount())
            .isEqualTo(1);

        assertThat(ensembleSummaryView.memberStatus())
            .isEqualTo("unknown");
    }

    @Test
    public void memberAcceptedIsTrueWhenMemberEnsembleParticipant() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        TestMemberBuilder memberBuilder = new TestMemberBuilder();
        Member member = memberBuilder
            .withFirstName("name")
            .withGithubUsername("participant_username")
            .buildAndSave();
        ensemble.acceptedBy(member.getId());

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView
            .toView(ensemble, member.getId(), memberBuilder.memberService());

        assertThat(ensembleSummaryView.memberStatus())
            .isEqualTo("accepted");
    }

    @Test
    public void spectatorIsInSpectatorList() throws Exception {
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

    @Test
    public void noRecordingEnsembleThenViewIncludesEmptyLink() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        MemberService memberService = new DefaultMemberService(new InMemoryMemberRepository());

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(1), memberService);

        assertThat(ensembleSummaryView.recordingLink())
            .isEmpty();
    }

    @Test
    public void ensembleWithRecordingThenViewIncludesStringOfLink() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        ensemble.linkToRecordingAt(URI.create("https://recording.link/abc123"));
        MemberService memberService = new DefaultMemberService(new InMemoryMemberRepository());

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(1), memberService);

        assertThat(ensembleSummaryView.recordingLink())
            .isEqualTo("https://recording.link/abc123");
    }

    @Test
    public void viewContainsGoogleCalendarLink() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        MemberService memberService = new DefaultMemberService(new InMemoryMemberRepository());

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(1), memberService);

        String expectedLink = new GoogleCalendarLinkCreator().createFor(ensemble);
        assertThat(ensembleSummaryView.googleCalendarLink())
            .isEqualTo(expectedLink);
    }

    @Test
    public void viewIndicatesNotAbleToAcceptIfEnsembleIsFullAndCurrentlyUnknown() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        EnsembleFactory.acceptCountMembersFor(5, ensemble);

        MemberId memberIdOfUnknownMember = MemberId.of(99L);
        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, memberIdOfUnknownMember, new StubMemberService());

        assertThat(ensembleSummaryView.memberStatus())
            .isEqualTo("full");
    }

    @Test
    public void viewIndicatesCanAcceptIfEnsembleIsNotFull() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        EnsembleFactory.acceptCountMembersFor(2, ensemble);

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(1), new StubMemberService());

        assertThat(ensembleSummaryView.memberStatus())
            .isEqualTo("accepted");
    }

    @Test
    public void ensembleWithNoOneAcceptedShowsNoAcceptedMembers() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        MemberService memberService = new DefaultMemberService(new InMemoryMemberRepository());

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView
            .toView(ensemble, MemberId.of(11L), memberService);

        assertThat(ensembleSummaryView.participants())
            .isEmpty();
    }

    @Test
    public void ensembleWithAcceptedMembersGetsInfoOnAndShowsWhoAccepted() throws Exception {
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
        ensemble.acceptedBy(member1.getId());
        ensemble.acceptedBy(member2.getId());

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
