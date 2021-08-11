package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.domain.Huddle;

import java.util.List;

public record HuddleDetailView(long id,
                               String name,
                               String startDateTime,
                               String duration,
                               String topic,
                               List<ParticipantView> participantViews) {

    static HuddleDetailView from(Huddle huddle) {
        List<ParticipantView> participantViews =
                huddle.participants().stream()
                      .map(ParticipantView::from)
                      .toList();
        return new HuddleDetailView(huddle.getId().id(),
                                    huddle.name(),
                                    DateTimeFormatting.formatAsDateTime(huddle.startDateTime()),
                                    "90m", "topic",
                                    participantViews);
    }

    public int size() {
        return participantViews.size();
    }
}
