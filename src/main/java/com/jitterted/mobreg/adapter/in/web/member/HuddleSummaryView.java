package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.application.GoogleCalendarLinkCreator;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberStatus;

import java.time.ZonedDateTime;
import java.util.List;

public record HuddleSummaryView(long id,
                                String name,
                                String zoomMeetingLink,
                                String dateTime,
                                String googleCalendarLink,
                                int numberRegistered,
                                boolean isCompleted,
                                String recordingLink,
                                boolean memberRegistered,
                                boolean canRegister) {

    public static List<HuddleSummaryView> from(List<Huddle> huddles, MemberId memberId) {
        return huddles.stream()
                      .filter(huddle -> huddle.statusFor(memberId, ZonedDateTime.now()) != MemberStatus.HIDDEN)
                      .map(huddle -> toView(huddle, memberId))
                      .toList();
    }

    public static HuddleSummaryView toView(Huddle huddle, MemberId memberId) {
        return new HuddleSummaryView(huddle.getId().id(),
                                     huddle.name(),
                                     huddle.zoomMeetingLink().toString(),
                                     DateTimeFormatting.formatAsDateTimeForJavaScriptDateIso8601(huddle.startDateTime()),
                                     new GoogleCalendarLinkCreator().createFor(huddle),
                                     huddle.registeredMemberCount(),
                                     huddle.isCompleted(),
                                     huddle.recordingLink().toString(),
                                     huddle.isAccepted(memberId),
                                     huddle.canRegister());
    }

}
