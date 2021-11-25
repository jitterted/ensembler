package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.application.GoogleCalendarLinkCreator;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleFactory;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberFactory;
import com.jitterted.mobreg.domain.MemberId;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.*;

class HuddleSummaryViewTest {

    @Test
    public void memberStatusUnknownWhenHuddleIsEmpty() throws Exception {
        Huddle huddle = HuddleFactory.createHuddleWithIdOf1AndOneDayInTheFuture();

        HuddleSummaryView huddleSummaryView =
                HuddleSummaryView.toView(huddle, MemberId.of(97L));

        assertThat(huddleSummaryView.memberStatus())
                .isEqualTo("unknown");
    }

    @Test
    public void withAnotherAcceptedMemberThenMemberAcceptedIsFalse() throws Exception {
        Huddle huddle = HuddleFactory.createHuddleWithIdOf1AndOneDayInTheFuture();
        Member member = new Member("name", "seven");
        MemberId memberId = MemberId.of(7L);
        member.setId(memberId);
        huddle.acceptedBy(memberId);

        HuddleSummaryView huddleSummaryView = HuddleSummaryView
                .toView(huddle, MemberId.of(5L));

        assertThat(huddleSummaryView.numberRegistered())
                .isEqualTo(1);

        assertThat(huddleSummaryView.memberStatus())
                .isEqualTo("unknown");
    }

    @Test
    public void memberAcceptedIsTrueWhenMemberHuddleParticipant() throws Exception {
        Huddle huddle = HuddleFactory.createHuddleWithIdOf1AndOneDayInTheFuture();
        Member member = new Member("name",
                                   "participant_username");
        MemberId memberId = MemberId.of(3L);
        member.setId(memberId);
        huddle.acceptedBy(memberId);

        HuddleSummaryView huddleSummaryView = HuddleSummaryView
                .toView(huddle, memberId);

        assertThat(huddleSummaryView.memberStatus())
                .isEqualTo("accepted");
    }

    @Test
    public void noRecordingHuddleThenViewIncludesEmptyLink() throws Exception {
        Huddle huddle = HuddleFactory.createHuddleWithIdOf1AndOneDayInTheFuture();

        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(huddle, MemberId.of(1));

        assertThat(huddleSummaryView.recordingLink())
                .isEmpty();
    }

    @Test
    public void huddleWithRecordingThenViewIncludesStringOfLink() throws Exception {
        Huddle huddle = HuddleFactory.createHuddleWithIdOf1AndOneDayInTheFuture();
        huddle.linkToRecordingAt(URI.create("https://recording.link/abc123"));

        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(huddle, MemberId.of(1));

        assertThat(huddleSummaryView.recordingLink())
                .isEqualTo("https://recording.link/abc123");
    }

    @Test
    public void viewContainsGoogleCalendarLink() throws Exception {
        Huddle huddle = HuddleFactory.createHuddleWithIdOf1AndOneDayInTheFuture();

        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(huddle, MemberId.of(1));

        String expectedLink = new GoogleCalendarLinkCreator().createFor(huddle);
        assertThat(huddleSummaryView.googleCalendarLink())
                .isEqualTo(expectedLink);
    }

    @Test
    public void viewIndicatesNotAbleToAcceptIfHuddleIsFullAndCurrentlyUnknown() throws Exception {
        Huddle huddle = HuddleFactory.createHuddleWithIdOf1AndOneDayInTheFuture();
        MemberFactory.registerCountMembersWithHuddle(huddle, 5);

        MemberId memberIdOfUnknownMember = MemberId.of(99L);
        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(huddle, memberIdOfUnknownMember);

        assertThat(huddleSummaryView.memberStatus())
                .isEqualTo("full");
    }
    
    @Test
    public void viewIndicatesCanAcceptIfHuddleIsNotFull() throws Exception {
        Huddle huddle = HuddleFactory.createHuddleWithIdOf1AndOneDayInTheFuture();
        MemberFactory.registerCountMembersWithHuddle(huddle, 2);

        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(huddle, MemberId.of(1));

        assertThat(huddleSummaryView.memberStatus())
                .isEqualTo("accepted");
    }

}