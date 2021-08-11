package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.port.MemberRepository;

import java.util.List;

public record HuddleDetailView(long id,
                               String name,
                               String startDateTime,
                               String duration,
                               String topic,
                               // TODO: rename to MemberView
                               List<ParticipantView> participantViews) {

    static HuddleDetailView from(Huddle huddle, MemberRepository memberRepository) {
        List<ParticipantView> participantViews =
                huddle.registeredMembers().stream()
                      .map(memberRepository::findById)
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
