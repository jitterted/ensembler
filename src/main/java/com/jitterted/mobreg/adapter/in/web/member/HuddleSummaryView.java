package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.application.GoogleCalendarLinkCreator;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberStatus;

import java.time.ZonedDateTime;
import java.util.List;

public record HuddleSummaryView(long id,
                                String name,
                                String zoomMeetingLink,
                                String dateTime,
                                String googleCalendarLink,
                                int numberRegistered,
                                String recordingLink,
                                String memberStatus) {

    public static List<HuddleSummaryView> from(List<Ensemble> ensembles, MemberId memberId) {
        return ensembles.stream()
                        .filter(huddle -> huddle.statusFor(memberId, ZonedDateTime.now()) != MemberStatus.HIDDEN)
                        .map(huddle -> toView(huddle, memberId))
                        .toList();
    }

    public static HuddleSummaryView toView(Ensemble ensemble, MemberId memberId) {
        return new HuddleSummaryView(ensemble.getId().id(),
                                     ensemble.name(),
                                     ensemble.zoomMeetingLink().toString(),
                                     DateTimeFormatting.formatAsDateTimeForJavaScriptDateIso8601(ensemble.startDateTime()),
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
