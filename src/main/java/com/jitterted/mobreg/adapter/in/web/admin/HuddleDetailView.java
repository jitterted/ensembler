package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.Huddle;

import java.util.List;

public record HuddleDetailView(long id,
                               String name,
                               String startDateTime,
                               String duration,
                               String zoomMeetingLink,
                               boolean isCompleted,
                               String recordingLink,
                               List<MemberView> memberViews) {

    static HuddleDetailView from(Huddle huddle, MemberService memberService) {
        List<MemberView> memberViews =
                huddle.acceptedMembers().stream()
                      .map(memberService::findById)
                      .map(MemberView::from)
                      .toList();
        return new HuddleDetailView(huddle.getId().id(),
                                    huddle.name(),
                                    DateTimeFormatting.formatAsDateTimeForJavaScriptDateIso8601(huddle.startDateTime()),
                                    "90m",
                                    huddle.zoomMeetingLink().toString(),
                                    huddle.isCompleted(),
                                    huddle.recordingLink().toString(),
                                    memberViews);
    }

    public int size() {
        return memberViews.size();
    }
}
