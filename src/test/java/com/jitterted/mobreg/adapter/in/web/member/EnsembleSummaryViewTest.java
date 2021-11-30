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
    public void memberStatusUnknownWhenHuddleIsEmpty() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();

        HuddleSummaryView huddleSummaryView =
                HuddleSummaryView.toView(ensemble, MemberId.of(97L));

        assertThat(huddleSummaryView.memberStatus())
                .isEqualTo("unknown");
    }

    @Test
    public void withAnotherAcceptedMemberThenMemberAcceptedIsFalse() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        Member member = new Member("name", "seven");
        MemberId memberId = MemberId.of(7L);
        member.setId(memberId);
        ensemble.acceptedBy(memberId);

        HuddleSummaryView huddleSummaryView = HuddleSummaryView
                .toView(ensemble, MemberId.of(5L));

        assertThat(huddleSummaryView.numberRegistered())
                .isEqualTo(1);

        assertThat(huddleSummaryView.memberStatus())
                .isEqualTo("unknown");
    }

    @Test
    public void memberAcceptedIsTrueWhenMemberHuddleParticipant() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        Member member = new Member("name",
                                   "participant_username");
        MemberId memberId = MemberId.of(3L);
        member.setId(memberId);
        ensemble.acceptedBy(memberId);

        HuddleSummaryView huddleSummaryView = HuddleSummaryView
                .toView(ensemble, memberId);

        assertThat(huddleSummaryView.memberStatus())
                .isEqualTo("accepted");
    }

    @Test
    public void noRecordingHuddleThenViewIncludesEmptyLink() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();

        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(ensemble, MemberId.of(1));

        assertThat(huddleSummaryView.recordingLink())
                .isEmpty();
    }

    @Test
    public void huddleWithRecordingThenViewIncludesStringOfLink() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        ensemble.linkToRecordingAt(URI.create("https://recording.link/abc123"));

        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(ensemble, MemberId.of(1));

        assertThat(huddleSummaryView.recordingLink())
                .isEqualTo("https://recording.link/abc123");
    }

    @Test
    public void viewContainsGoogleCalendarLink() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();

        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(ensemble, MemberId.of(1));

        String expectedLink = new GoogleCalendarLinkCreator().createFor(ensemble);
        assertThat(huddleSummaryView.googleCalendarLink())
                .isEqualTo(expectedLink);
    }

    @Test
    public void viewIndicatesNotAbleToAcceptIfHuddleIsFullAndCurrentlyUnknown() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        MemberFactory.registerCountMembersWith(ensemble, 5);

        MemberId memberIdOfUnknownMember = MemberId.of(99L);
        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(ensemble, memberIdOfUnknownMember);

        assertThat(huddleSummaryView.memberStatus())
                .isEqualTo("full");
    }
    
    @Test
    public void viewIndicatesCanAcceptIfHuddleIsNotFull() throws Exception {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();
        MemberFactory.registerCountMembersWith(ensemble, 2);

        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(ensemble, MemberId.of(1));

        assertThat(huddleSummaryView.memberStatus())
                .isEqualTo("accepted");
    }

}