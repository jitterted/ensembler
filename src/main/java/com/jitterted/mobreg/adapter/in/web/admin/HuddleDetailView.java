package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.Ensemble;

import java.util.List;

public record HuddleDetailView(long id,
                               String name,
                               String startDateTime,
                               String duration,
                               String zoomMeetingLink,
                               boolean isCompleted,
                               String recordingLink,
                               List<MemberView> memberViews) {

    static HuddleDetailView from(Ensemble ensemble, MemberService memberService) {
        List<MemberView> memberViews =
                ensemble.acceptedMembers().stream()
                        .map(memberService::findById)
                        .map(MemberView::from)
                        .toList();
        return new HuddleDetailView(ensemble.getId().id(),
                                    ensemble.name(),
                                    DateTimeFormatting.formatAsDateTimeForJavaScriptDateIso8601(ensemble.startDateTime()),
                                    "90m",
                                    ensemble.zoomMeetingLink().toString(),
                                    ensemble.isCompleted(),
                                    ensemble.recordingLink().toString(),
                                    memberViews);
    }

    public int size() {
        return memberViews.size();
    }
}
