package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.domain.Huddle;

import java.util.List;

public record HuddleSummaryView(long id,
                                String name,
                                String dateTime,
                                int numberRegistered,
                                boolean memberRegistered) {
    public static List<HuddleSummaryView> from(List<Huddle> huddles, String username) {
        return huddles.stream()
                      .map(huddle -> toView(huddle, username))
                      .toList();
    }

    public static HuddleSummaryView toView(Huddle huddle, String username) {
        return new HuddleSummaryView(huddle.getId().id(),
                                     huddle.name(),
                                     DateTimeFormatting.formatAsDateTime(huddle.startDateTime()),
                                     huddle.numberRegistered(),
                                     huddle.isRegisteredByUsername(username));
    }
}
