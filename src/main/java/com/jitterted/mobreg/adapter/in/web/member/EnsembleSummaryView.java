package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.adapter.in.web.admin.MemberView;
import com.jitterted.mobreg.application.GoogleCalendarLinkCreator;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberStatus;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

public record EnsembleSummaryView(long id,
                                  String name,
                                  String dateTime,
                                  int participantCount,
                                  List<MemberView> participants,
                                  List<MemberView> spectators,
                                  String memberStatus,
                                  String zoomMeetingLink,
                                  String googleCalendarLink,
                                  String recordingLink) {

    public static List<EnsembleSummaryView> from(List<Ensemble> ensembles, MemberId memberId, MemberService memberService) {
        return ensembles.stream()
                        .filter(ensemble -> ensemble.statusFor(memberId, ZonedDateTime.now()) != MemberStatus.HIDDEN)
                        .map(ensemble -> toView(ensemble, memberId, memberService))
                        .toList();
    }

    public static EnsembleSummaryView toView(Ensemble ensemble, MemberId memberId, MemberService memberService) {
        List<MemberView> participantViews = transform(memberService, ensemble.acceptedMembers());
        List<MemberView> spectatorViews = transform(memberService, ensemble.spectators());
        return new EnsembleSummaryView(ensemble.getId().id(),
                                       ensemble.name(),
                                       DateTimeFormatting.formatAsDateTimeForCommonIso8601(ensemble.startDateTime()),
                                       ensemble.acceptedCount(),
                                       participantViews,
                                       spectatorViews,
                                       memberStatusToViewString(ensemble, memberId),
                                       ensemble.meetingLink().toString(),
                                       new GoogleCalendarLinkCreator().createFor(ensemble),
                                       ensemble.recordingLink().toString()
        );
    }

    private static String memberStatusToViewString(Ensemble ensemble, MemberId memberId) {
        return ensemble.statusFor(memberId, ZonedDateTime.now())
                       .toString()
                       .toLowerCase();
    }

    private static List<MemberView> transform(MemberService memberService, Stream<MemberId> memberIdStream) {
        return memberIdStream
            .map(memberService::findById)
            .map(MemberView::from)
            .toList();
    }
}
