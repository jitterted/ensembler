package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.MemberService;

import java.util.List;

public record HuddleDetailView(long id,
                               String name,
                               String startDateTime,
                               String duration,
                               String zoomMeetingLink,
                               List<MemberView> memberViews) {

    static HuddleDetailView from(Huddle huddle, MemberService memberService) {
        List<MemberView> memberViews =
                huddle.registeredMembers().stream()
                      .map(memberService::findById)
                      .map(MemberView::from)
                      .toList();
        return new HuddleDetailView(huddle.getId().id(),
                                    huddle.name(),
                                    DateTimeFormatting.formatAsDateTime(huddle.startDateTime()),
                                    "90m",
                                    huddle.zoomMeetingLink().toString(),
                                    memberViews);
    }

    public int size() {
        return memberViews.size();
    }
}
