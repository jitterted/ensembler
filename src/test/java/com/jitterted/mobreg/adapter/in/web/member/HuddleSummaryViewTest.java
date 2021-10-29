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
    public void memberRegisteredIsFalseWhenHuddleIsEmpty() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleWithIdOf1();

        HuddleSummaryView huddleSummaryView =
                HuddleSummaryView.toView(huddle, MemberId.of(97L));

        assertThat(huddleSummaryView.memberRegistered())
                .isFalse();
    }

    @Test
    public void withAnotherRegisteredMemberThenMemberRegisteredIsFalse() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleWithIdOf1();
        Member member = new Member("name", "seven");
        MemberId memberId = MemberId.of(7L);
        member.setId(memberId);
        huddle.register(memberId);

        HuddleSummaryView huddleSummaryView = HuddleSummaryView
                .toView(huddle, MemberId.of(5L));

        assertThat(huddleSummaryView.numberRegistered())
                .isEqualTo(1);

        assertThat(huddleSummaryView.memberRegistered())
                .isFalse();
    }

    @Test
    public void memberRegisteredIsTrueWhenMemberHuddleParticipant() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleWithIdOf1();
        Member member = new Member("name",
                                   "participant_username");
        MemberId memberId = MemberId.of(3L);
        member.setId(memberId);
        huddle.register(memberId);

        HuddleSummaryView huddleSummaryView = HuddleSummaryView
                .toView(huddle, memberId);

        assertThat(huddleSummaryView.memberRegistered())
                .isTrue();
    }

    @Test
    public void noRecordingHuddleThenViewIncludesEmptyLink() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleWithIdOf1();

        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(huddle, MemberId.of(1));

        assertThat(huddleSummaryView.recordingLink())
                .isEmpty();
    }

    @Test
    public void huddleWithRecordingThenViewIncludesStringOfLink() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleWithIdOf1();
        huddle.linkToRecordingAt(URI.create("https://recording.link/abc123"));

        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(huddle, MemberId.of(1));

        assertThat(huddleSummaryView.recordingLink())
                .isEqualTo("https://recording.link/abc123");
    }

    @Test
    public void viewContainsGoogleCalendarLink() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleWithIdOf1();

        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(huddle, MemberId.of(1));

        String expectedLink = new GoogleCalendarLinkCreator().createFor(huddle);
        assertThat(huddleSummaryView.googleCalendarLink())
                .isEqualTo(expectedLink);
    }

    @Test
    public void viewIndicatesNotAbleToRegisterIfHuddleIsFull() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleWithIdOf1();
        MemberFactory.registerCountMembersWithHuddle(huddle, 5);

        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(huddle, MemberId.of(1));

        assertThat(huddleSummaryView.canRegister())
                .isFalse();
    }
    
    @Test
    public void viewIndicatesCanRegisterIfHuddleIsNotFull() throws Exception {
        Huddle huddle = HuddleFactory.createDefaultHuddleWithIdOf1();
        MemberFactory.registerCountMembersWithHuddle(huddle, 2);

        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(huddle, MemberId.of(1));

        assertThat(huddleSummaryView.canRegister())
                .isTrue();
    }

}