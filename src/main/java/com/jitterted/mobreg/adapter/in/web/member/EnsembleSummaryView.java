package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.adapter.in.web.admin.MemberView;
import com.jitterted.mobreg.application.GoogleCalendarLinkCreator;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.MemberEnsembleStatus;
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
                                  @Deprecated String memberStatus,
                                  String zoomMeetingLink,
                                  String googleCalendarLink,
                                  String recordingLink,
                                  SpectatorAction spectatorAction,
                                  ParticipantAction participantAction) {

    public static List<EnsembleSummaryView> from(List<Ensemble> ensembles, MemberId memberId, MemberService memberService) {
        return ensembles.stream()
                        .filter(ensemble -> ensemble.statusFor(memberId, ZonedDateTime.now()) != MemberEnsembleStatus.HIDDEN)
                        .map(ensemble -> toView(ensemble, memberId, memberService))
                        .toList();
    }

    public static EnsembleSummaryView toView(Ensemble ensemble, MemberId memberId, MemberService memberService) {
        List<MemberView> participantViews = transform(memberService, ensemble.acceptedMembers());
        List<MemberView> spectatorViews = transform(memberService, ensemble.spectators());

        String memberStatus = memberStatusToViewString(ensemble, memberId);
        SpectatorAction spectatorAction = SpectatorAction.from(ensemble.memberStatusFor(memberId));
        ParticipantAction participantAction = ParticipantAction.from(ensemble.memberStatusFor(memberId), ensemble.isFull());

        return new EnsembleSummaryView(
                ensemble.getId().id(),
                ensemble.name(),
                DateTimeFormatting.formatAsDateTimeForCommonIso8601(ensemble.startDateTime()),
                ensemble.acceptedCount(),
                participantViews,
                spectatorViews,
                memberStatus,
                ensemble.meetingLink().toString(),
                new GoogleCalendarLinkCreator().createFor(ensemble),
                ensemble.recordingLink().toString(),
                spectatorAction,
                participantAction
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

record SpectatorAction(String actionUrl, String buttonText) {
    public static SpectatorAction from(MemberStatus memberStatus) {
        return switch (memberStatus) {
            case UNKNOWN, DECLINED -> new SpectatorAction(
                    "/member/join-as-spectator",
                    "Join as Spectator &#x1F440;");
            case PARTICIPANT -> new SpectatorAction(
                    "/member/join-as-spectator",
                    "Switch to Spectator &#x1F440;");
            case SPECTATOR -> new SpectatorAction(
                    "/member/decline",
                    "Leave Spectators &#x1f44b;");
        };
    }
}

record ParticipantAction(String actionUrl, String buttonText, boolean disabled) {

    public static ParticipantAction from(MemberStatus memberStatus, boolean disabled) {
        return switch (memberStatus) {
            case UNKNOWN, DECLINED -> new ParticipantAction(
                    "/member/accept",
                    "Participate in Rotation &#x2328;",
                    disabled);
            case PARTICIPANT -> new ParticipantAction(
                    "/member/decline",
                    "Leave Rotation &#x1f44b;",
                    false); // can always leave
            case SPECTATOR -> new ParticipantAction(
                    "/member/accept",
                    "Switch to Participant &#x1f44b;",
                    disabled);
        };
    }

}
