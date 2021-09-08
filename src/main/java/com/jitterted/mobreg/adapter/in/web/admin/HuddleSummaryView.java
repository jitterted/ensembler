package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.domain.Huddle;

import java.util.List;

public record HuddleSummaryView(long id,
                                String name,
                                String zoomMeetingLink,
                                String dateTime,
                                boolean isCompleted,
                                int numberRegistered) {

    public static List<HuddleSummaryView> from(List<Huddle> huddles) {
        return huddles.stream()
                      .map(HuddleSummaryView::toView)
                      .toList();
    }

    public static HuddleSummaryView toView(Huddle huddle) {
        return new HuddleSummaryView(huddle.getId().id(),
                                     huddle.name(),
                                     huddle.zoomMeetingLink().toString(),
                                     DateTimeFormatting.formatAsDateTime(huddle.startDateTime()),
                                     huddle.isCompleted(),
                                     huddle.registeredMemberCount());
    }
}
