package com.jitterted.moborg.adapter.in.web;

import com.jitterted.moborg.adapter.DateTimeFormatting;
import com.jitterted.moborg.domain.Huddle;

public record HuddleDetailView(long id,
                               String name,
                               String startDateTime,
                               String duration,
                               String topic,
                               int size) {

  static HuddleDetailView from(Huddle huddle) {
    return new HuddleDetailView(huddle.getId().id(),
                                huddle.name(),
                                DateTimeFormatting.formatAsDateTime(huddle.startDateTime()),
                                "90m", "topic", 3);
  }
}
