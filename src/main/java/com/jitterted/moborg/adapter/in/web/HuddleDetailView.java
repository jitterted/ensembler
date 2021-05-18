package com.jitterted.moborg.adapter.in.web;

import com.jitterted.moborg.adapter.DateTimeFormatting;
import com.jitterted.moborg.domain.Huddle;

import java.util.List;

public record HuddleDetailView(long id,
                               String name,
                               String startDateTime,
                               String duration,
                               String topic,
                               List<ParticipantView> participantViews) {

    static HuddleDetailView from(Huddle huddle) {
        List<ParticipantView> participantViews = huddle.participants().stream()
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
