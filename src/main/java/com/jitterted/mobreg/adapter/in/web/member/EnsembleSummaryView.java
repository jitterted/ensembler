package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.adapter.in.web.admin.MemberView;
import com.jitterted.mobreg.application.GoogleCalendarLinkCreator;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberStatus;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record EnsembleSummaryView(long id,
                                  String name,
                                  String zoomMeetingLink,
                                  String dateTime,
                                  String googleCalendarLink,
                                  int numberRegistered,
                                  String recordingLink,
                                  String memberStatus,
                                  List<MemberView> acceptedMembers) {

    public static List<EnsembleSummaryView> from(List<Ensemble> ensembles, MemberId memberId, List<Member> allUsers) {
        return ensembles.stream()
                        .filter(ensemble -> ensemble.statusFor(memberId, ZonedDateTime.now()) != MemberStatus.HIDDEN)
                        .map(ensemble -> toView(ensemble, memberId, allUsers))
                        .toList();
    }

    public static EnsembleSummaryView toView(Ensemble ensemble, MemberId memberId, List<Member> allUsers) {
        return new EnsembleSummaryView(ensemble.getId().id(),
                                       ensemble.name(),
                                       ensemble.meetingLink().toString(),
                                       DateTimeFormatting.formatAsDateTimeForCommonIso8601(ensemble.startDateTime()),
                                       new GoogleCalendarLinkCreator().createFor(ensemble),
                                       ensemble.acceptedCount(),
                                       ensemble.recordingLink().toString(),
                                       memberStatusToViewString(ensemble, memberId),
                                       acceptedMemberViews(ensemble, allUsers));
    }

    public static EnsembleSummaryView toView(Ensemble ensemble, MemberId memberId, MemberService memberService) {
        List<MemberView> acceptedMembers = transform(memberService, ensemble.acceptedMembers());
        return new EnsembleSummaryView(ensemble.getId().id(),
                                       ensemble.name(),
                                       ensemble.meetingLink().toString(),
                                       DateTimeFormatting.formatAsDateTimeForCommonIso8601(ensemble.startDateTime()),
                                       new GoogleCalendarLinkCreator().createFor(ensemble),
                                       ensemble.acceptedCount(),
                                       ensemble.recordingLink().toString(),
                                       memberStatusToViewString(ensemble, memberId),
                                       acceptedMembers);
    }

    private static List<MemberView> transform(MemberService memberService, Stream<MemberId> memberIdStream) {
        return memberIdStream
            .map(memberService::findById)
            .map(MemberView::from)
            .toList();
    }

    private static String memberStatusToViewString(Ensemble ensemble, MemberId memberId) {
        return ensemble.statusFor(memberId, ZonedDateTime.now())
                       .toString()
                       .toLowerCase();
    }

    private static List<MemberView> acceptedMemberViews(Ensemble ensemble, List<Member> allExistingUsers) {
        Set<Long> acceptedMemberIds = acceptedMemberIds(ensemble);
        return allExistingUsers.stream()
                       .filter(user -> acceptedMemberIds.contains(user.getId().id()))
                       .map(MemberView::from)
                       .collect(Collectors.toList());
    }

    private static Set<Long> acceptedMemberIds(Ensemble ensemble) {
        return ensemble.acceptedMembers()
                       .map(MemberId::id)
                       .collect(Collectors.toSet());
    }
}
