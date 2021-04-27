package com.jitterted.moborg.adapter.in.web;

import com.jitterted.moborg.adapter.DateTimeFormatting;
import com.jitterted.moborg.domain.Huddle;

import java.util.List;

public record HuddleSummaryView(String name,
                                String dateTime,
                                int numberRegistered) {
  public static List<HuddleSummaryView> from(List<Huddle> huddles) {
    return huddles.stream()
                  .map(HuddleSummaryView::toView)
                  .toList();
  }

  public static HuddleSummaryView toView(Huddle huddle) {
    return new HuddleSummaryView(huddle.name(),
                                 DateTimeFormatting.formatAsDateTime(huddle.startDateTime()),
                                 huddle.numberRegistered());
  }
}
