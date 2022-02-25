package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.application.GoogleCalendarLinkCreator;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberStatus;

import java.time.ZonedDateTime;
import java.util.List;

public record EnsembleSummaryView(long id,
                                  String name,
                                  String zoomMeetingLink,
                                  String dateTime,
                                  String googleCalendarLink,
                                  int numberRegistered,
                                  String recordingLink,
                                  String memberStatus) {

    public static List<EnsembleSummaryView> from(List<Ensemble> ensembles, MemberId memberId) {
        return ensembles.stream()
                        .filter(ensemble -> ensemble.statusFor(memberId, ZonedDateTime.now()) != MemberStatus.HIDDEN)
                        .map(ensemble -> toView(ensemble, memberId))
                        .toList();
    }

    public static EnsembleSummaryView toView(Ensemble ensemble, MemberId memberId) {
        return new EnsembleSummaryView(ensemble.getId().id(),
                                       ensemble.name(),
                                       ensemble.meetingLink().toString(),
                                       DateTimeFormatting.formatAsDateTimeForCommonIso8601(ensemble.startDateTime()),
                                       new GoogleCalendarLinkCreator().createFor(ensemble),
                                       ensemble.acceptedCount(),
                                       ensemble.recordingLink().toString(),
                                       memberStatusToViewString(ensemble, memberId));
    }

    private static String memberStatusToViewString(Ensemble ensemble, MemberId memberId) {
        return ensemble.statusFor(memberId, ZonedDateTime.now())
                       .toString()
                       .toLowerCase();
    }

}
