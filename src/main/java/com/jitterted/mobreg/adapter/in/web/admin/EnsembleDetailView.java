package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.MemberId;

import java.util.List;
import java.util.stream.Stream;

record EnsembleDetailView(long id,
                                 String name,
                                 String startDateTime,
                                 String duration,
                                 String zoomMeetingLink,
                                 String state,
                                 String recordingLink,
                                 List<MemberView> acceptedMembers,
                                 List<MemberView> declinedMembers) {

    static EnsembleDetailView from(Ensemble ensemble, MemberService memberService) {
        List<MemberView> acceptedMembers = transform(memberService, ensemble.participants());
        List<MemberView> declinedMembers = transform(memberService, ensemble.declinedMembers());
        return new EnsembleDetailView(ensemble.getId().id(),
                                      ensemble.name(),
                                      DateTimeFormatting.formatAsDateTimeForCommonIso8601(ensemble.startDateTime()),
                                      "90m",
                                      ensemble.meetingLink().toString(),
                                      ensemble.state().toString().toLowerCase(),
                                      ensemble.recordingLink().toString(),
                                      acceptedMembers,
                                      declinedMembers);
    }

    private static List<MemberView> transform(MemberService memberService, Stream<MemberId> memberIdStream) {
        return memberIdStream
                .map(memberService::findById)
                .map(MemberView::from)
                .toList();
    }

    public int size() {
        return acceptedMembers.size();
    }

}
