package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.MemberId;

import java.util.List;

public record HuddleSummaryView(long id,
                                String name,
                                String zoomMeetingLink,
                                String dateTime,
                                int numberRegistered,
                                boolean isCompleted,
                                String recordingLink,
                                boolean memberRegistered) {

    public static List<HuddleSummaryView> from(List<Huddle> huddles, MemberId memberId) {
        return huddles.stream()
                      .map(huddle -> toView(huddle, memberId))
                      .toList();
    }

    public static HuddleSummaryView toView(Huddle huddle, MemberId memberId) {
        return new HuddleSummaryView(huddle.getId().id(),
                                     huddle.name(),
                                     huddle.zoomMeetingLink().toString(),
                                     DateTimeFormatting.formatAsDateTimeForJavaScriptDateIso8601(huddle.startDateTime()),
                                     huddle.registeredMemberCount(),
                                     huddle.isCompleted(),
                                     huddle.recordingLink().toString(),
                                     huddle.isRegisteredById(memberId));
    }
}
