package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.in.web.admin.MemberView;
import com.jitterted.mobreg.application.GoogleCalendarLinkCreator;
import com.jitterted.mobreg.application.MemberFactory;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.TestMemberBuilder;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleFactory;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EnsembleSummaryViewTest {

    @Test
    public void memberStatusUnknownWhenEnsembleIsEmpty() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();

        EnsembleSummaryView ensembleSummaryView =
            EnsembleSummaryView.toView(ensemble, MemberId.of(97L), List.of());

        assertThat(ensembleSummaryView.memberStatus())
                .isEqualTo("unknown");
    }

    @Test
    public void withAnotherAcceptedMemberThenMemberAcceptedIsFalse() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        Member member = new Member("name", "seven");
        MemberId memberId = MemberId.of(7L);
        member.setId(memberId);
        ensemble.acceptedBy(memberId);
        List<Member> allRegisteredMembers = List.of(member, MemberFactory.createMember(5L, "other", "other_github"));

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView
            .toView(ensemble, MemberId.of(5L), allRegisteredMembers);

        assertThat(ensembleSummaryView.numberRegistered())
                .isEqualTo(1);

        assertThat(ensembleSummaryView.memberStatus())
                .isEqualTo("unknown");
    }

    @Test
    public void memberAcceptedIsTrueWhenMemberEnsembleParticipant() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        Member member = new Member("name",
                                   "participant_username");
        MemberId memberId = MemberId.of(3L);
        member.setId(memberId);
        ensemble.acceptedBy(memberId);

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView
            .toView(ensemble, memberId, List.of(member));

        assertThat(ensembleSummaryView.memberStatus())
                .isEqualTo("accepted");
    }

    @Test
    public void noRecordingEnsembleThenViewIncludesEmptyLink() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(1), List.of());

        assertThat(ensembleSummaryView.recordingLink())
                .isEmpty();
    }

    @Test
    public void ensembleWithRecordingThenViewIncludesStringOfLink() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        ensemble.linkToRecordingAt(URI.create("https://recording.link/abc123"));

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(1), List.of());

        assertThat(ensembleSummaryView.recordingLink())
                .isEqualTo("https://recording.link/abc123");
    }

    @Test
    public void viewContainsGoogleCalendarLink() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(1), List.of());

        String expectedLink = new GoogleCalendarLinkCreator().createFor(ensemble);
        assertThat(ensembleSummaryView.googleCalendarLink())
                .isEqualTo(expectedLink);
    }

    @Test
    public void viewIndicatesNotAbleToAcceptIfEnsembleIsFullAndCurrentlyUnknown() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        MemberFactory.acceptCountMembersWith(ensemble, 5);
        List<Member> allRegisteredMembers = MemberFactory.createAcceptedMembersFrom(ensemble);

        MemberId memberIdOfUnknownMember = MemberId.of(99L);
        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, memberIdOfUnknownMember, allRegisteredMembers);

        assertThat(ensembleSummaryView.memberStatus())
                .isEqualTo("full");
    }

    @Test
    public void viewIndicatesCanAcceptIfEnsembleIsNotFull() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        MemberFactory.acceptCountMembersWith(ensemble, 2);
        List<Member> allRegisteredMembers = MemberFactory.createAcceptedMembersFrom(ensemble);

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(1), allRegisteredMembers);

        assertThat(ensembleSummaryView.memberStatus())
                .isEqualTo("accepted");
    }

    @Test
    public void ensembleWithNoOneAcceptedShowsNoAcceptedMembers() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView
            .toView(ensemble, MemberId.of(11L), List.of());

        assertThat(ensembleSummaryView.acceptedMembers())
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

        assertThat(ensembleSummaryView.acceptedMembers())
            .extracting(MemberView::firstName)
            .containsExactlyInAnyOrder("one", "two");
        assertThat(ensembleSummaryView.acceptedMembers())
            .extracting(MemberView::githubUsername)
            .containsExactlyInAnyOrder("github_one", "github_two");
    }

    @Test
    public void ensembleWithDeclinedMembersShowsNoAcceptedMembers() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        TestMemberBuilder memberBuilder = new TestMemberBuilder();
        MemberService memberService = memberBuilder.memberService();
        Member member = memberBuilder
            .withFirstName("one")
            .withGithubUsername("github_one")
            .buildAndSave();
        ensemble.declinedBy(member.getId());

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView
            .toView(ensemble, MemberId.of(member.getId().id()), memberService);

        assertThat(ensembleSummaryView.acceptedMembers())
            .isEmpty();
    }
}
