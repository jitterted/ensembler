package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.adapter.in.web.admin.MemberView;
import com.jitterted.mobreg.application.GoogleCalendarLinkCreator;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberStatus;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private static String memberStatusToViewString(Ensemble ensemble, MemberId memberId) {
        return ensemble.statusFor(memberId, ZonedDateTime.now())
                       .toString()
                       .toLowerCase();
    }

    private static List<MemberView> acceptedMemberViews(Ensemble ensemble, List<Member> allExistingUsers) {
        if (ensemble.acceptedCount() == 0 || allExistingUsers.isEmpty()) {
            return Collections.emptyList();
        }

        Map<MemberId, Member> membersById = allExistingUsers.stream()
            .collect(Collectors.toMap(Member::getId, Function.identity()));

        return ensemble.acceptedMembers()
            .map(membersById::get)
            .map(MemberView::from)
            .collect(Collectors.toList());
    }
}
