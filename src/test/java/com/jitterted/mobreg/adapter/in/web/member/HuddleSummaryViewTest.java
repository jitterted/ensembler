package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleSummaryViewTest {

    @Test
    public void memberRegisteredIsFalseWhenHuddleIsEmpty() throws Exception {
        Huddle huddle = createTestHuddle();

        HuddleSummaryView huddleSummaryView =
                HuddleSummaryView.toView(huddle, MemberId.of(97L));

        assertThat(huddleSummaryView.memberRegistered())
                .isFalse();
    }

    @Test
    public void withAnotherRegisteredMemberThenMemberRegisteredIsFalse() throws Exception {
        Huddle huddle = createTestHuddle();
        Member member = new Member("name", "seven");
        MemberId memberId = MemberId.of(7L);
        member.setId(memberId);
        huddle.registerById(memberId);

        HuddleSummaryView huddleSummaryView = HuddleSummaryView
                .toView(huddle, MemberId.of(5L));

        assertThat(huddleSummaryView.numberRegistered())
                .isEqualTo(1);

        assertThat(huddleSummaryView.memberRegistered())
                .isFalse();
    }

    @Test
    public void memberRegisteredIsTrueWhenMemberHuddleParticipant() throws Exception {
        Huddle huddle = createTestHuddle();
        Member member = new Member("name",
                                   "participant_username");
        MemberId memberId = MemberId.of(3L);
        member.setId(memberId);
        huddle.registerById(memberId);

        HuddleSummaryView huddleSummaryView = HuddleSummaryView
                .toView(huddle, memberId);

        assertThat(huddleSummaryView.memberRegistered())
                .isTrue();
    }

    @Test
    public void noRecordingHuddleThenViewIncludesEmptyLink() throws Exception {
        Huddle huddle = createTestHuddle();

        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(huddle, MemberId.of(1));

        assertThat(huddleSummaryView.recordingLink())
                .isEmpty();
    }

    @Test
    public void huddleWithRecordingThenViewIncludesStringOfLink() throws Exception {
        Huddle huddle = createTestHuddle();
        huddle.linkToRecordingAt(URI.create("https://recording.link/abc123"));

        HuddleSummaryView huddleSummaryView = HuddleSummaryView.toView(huddle, MemberId.of(1));

        assertThat(huddleSummaryView.recordingLink())
                .isEqualTo("https://recording.link/abc123");
    }

    @NotNull
    private Huddle createTestHuddle() {
        Huddle huddle = new Huddle("test", ZonedDateTime.now());
        huddle.setId(HuddleId.of(1L));
        return huddle;
    }
}