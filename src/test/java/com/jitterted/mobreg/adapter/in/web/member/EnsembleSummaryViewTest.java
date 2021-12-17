package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.application.GoogleCalendarLinkCreator;
import com.jitterted.mobreg.application.MemberFactory;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleFactory;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.*;

class EnsembleSummaryViewTest {

    @Test
    public void memberStatusUnknownWhenEnsembleIsEmpty() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();

        EnsembleSummaryView ensembleSummaryView =
                EnsembleSummaryView.toView(ensemble, MemberId.of(97L));

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

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView
                .toView(ensemble, MemberId.of(5L));

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
                .toView(ensemble, memberId);

        assertThat(ensembleSummaryView.memberStatus())
                .isEqualTo("accepted");
    }

    @Test
    public void noRecordingEnsembleThenViewIncludesEmptyLink() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(1));

        assertThat(ensembleSummaryView.recordingLink())
                .isEmpty();
    }

    @Test
    public void ensembleWithRecordingThenViewIncludesStringOfLink() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        ensemble.linkToRecordingAt(URI.create("https://recording.link/abc123"));

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(1));

        assertThat(ensembleSummaryView.recordingLink())
                .isEqualTo("https://recording.link/abc123");
    }

    @Test
    public void viewContainsGoogleCalendarLink() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(1));

        String expectedLink = new GoogleCalendarLinkCreator().createFor(ensemble);
        assertThat(ensembleSummaryView.googleCalendarLink())
                .isEqualTo(expectedLink);
    }

    @Test
    public void viewIndicatesNotAbleToAcceptIfEnsembleIsFullAndCurrentlyUnknown() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        MemberFactory.acceptCountMembersWith(ensemble, 5);

        MemberId memberIdOfUnknownMember = MemberId.of(99L);
        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, memberIdOfUnknownMember);

        assertThat(ensembleSummaryView.memberStatus())
                .isEqualTo("full");
    }
    
    @Test
    public void viewIndicatesCanAcceptIfEnsembleIsNotFull() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        MemberFactory.acceptCountMembersWith(ensemble, 2);

        EnsembleSummaryView ensembleSummaryView = EnsembleSummaryView.toView(ensemble, MemberId.of(1));

        assertThat(ensembleSummaryView.memberStatus())
                .isEqualTo("accepted");
    }

}