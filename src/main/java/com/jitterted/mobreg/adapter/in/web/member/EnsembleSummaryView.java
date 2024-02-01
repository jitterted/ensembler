package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.adapter.in.web.admin.MemberView;
import com.jitterted.mobreg.application.GoogleCalendarLinkCreator;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberStatus;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record EnsembleSummaryView(long id,
                                  String name,
                                  String dateTime,
                                  int participantCount,
                                  List<MemberView> participants,
                                  List<MemberView> spectators,
                                  Status status,
                                  boolean showActionButtons,
                                  SpectatorAction spectatorAction,
                                  ParticipantAction participantAction,
                                  boolean inProgress) {

    public static List<EnsembleSummaryView> from(List<Ensemble> ensembles, MemberId memberId, MemberService memberService, EnsembleSortOrder sortOrder) {
        return ensembles.stream()
                        .sorted(sortOrder.comparator())
                        .map(ensemble -> toView(ensemble, memberId, memberService))
                        .toList();
    }

    public static EnsembleSummaryView toView(Ensemble ensemble, MemberId memberId, MemberService memberService) {
        List<MemberView> participantViews = transform(memberService, ensemble.participants());
        List<MemberView> spectatorViews = transform(memberService, ensemble.spectators());

        MemberStatus memberStatusForEnsemble = ensemble.memberStatusFor(memberId);
        SpectatorAction spectatorAction = SpectatorAction.from(memberStatusForEnsemble);
        ParticipantAction participantAction = ParticipantAction.from(memberStatusForEnsemble,
                                                                     ensemble.isFull());

        return new EnsembleSummaryView(
                ensemble.getId().id(),
                ensemble.name(),
                DateTimeFormatting.formatAsDateTimeForCommonIso8601(ensemble.startDateTime()),
                ensemble.acceptedCount(),
                participantViews,
                spectatorViews,
                createStatusFor(ensemble, memberStatusForEnsemble),
                showActionButtonsFor(ensemble),
                spectatorAction,
                participantAction,
                false);
    }

    private static boolean showActionButtonsFor(Ensemble ensemble) {
        return !ensemble.endTimeIsInThePast(ZonedDateTime.now());
    }

    static Status createStatusFor(Ensemble ensemble, MemberStatus memberStatusForEnsemble) {
        if (ensemble.isPendingCompletedAsOf(ZonedDateTime.now())) {
            return Status.messageOnly("Recording Coming Soon...");
        }

        if (ensemble.isCanceled()) {
            return Status.messageOnly("Ensemble Was Canceled");
        }

        if (ensemble.isCompleted()) {
            return Status.linksOnly(List.of(
                    new DisplayLink(ensemble.recordingLink().toString(),
                                    "Recording Link")));
        }

        return switch (memberStatusForEnsemble) {
            case UNKNOWN, DECLINED -> Status.empty();
            case PARTICIPANT, SPECTATOR -> {
                DisplayLink calendarLink = new DisplayLink(
                        new GoogleCalendarLinkCreator().createFor(ensemble),
                        "<i class=\"fas fa-calendar-plus pr-2\" aria-hidden=\"true\"></i>Add to Google Calendar");
                DisplayLink zoomLink = new DisplayLink(
                        ensemble.meetingLink().toString(),
                        "<i class=\"far fa-video pr-2\" aria-hidden=\"true\"></i>Zoom Link");
                yield Status.linksOnly(List.of(calendarLink, zoomLink));
            }
        };
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

    public static ParticipantAction from(MemberStatus memberStatus, boolean ensembleFull) {
        if (ensembleFull && memberStatus != MemberStatus.PARTICIPANT) {
            return new ParticipantAction(
                    "",
                    "Cannot Participate: Ensemble Full",
                    true);
        }
        return switch (memberStatus) {
            case UNKNOWN, DECLINED -> new ParticipantAction(
                    "/member/accept",
                    "Participate in Rotation &#x2328;",
                    false);
            case PARTICIPANT -> new ParticipantAction(
                    "/member/decline",
                    "Leave Rotation &#x1f44b;",
                    false); // can always leave
            case SPECTATOR -> new ParticipantAction(
                    "/member/accept",
                    "Switch to Participant &#x2328;",
                    false);
        };
    }
}

record DisplayLink(String url, String text) {
}

class Status {
    private final List<DisplayLink> links;
    private final List<String> texts;

    private Status(List<DisplayLink> links, List<String> texts) {
        this.links = links;
        this.texts = texts;
    }

    public static Status messageOnly(String message) {
        return new Status(Collections.emptyList(),
                          List.of(message));
    }

    public static Status linksOnly(List<DisplayLink> displayLinks) {
        return new Status(displayLinks,
                          Collections.emptyList());
    }

    public static Status empty() {
        return new Status(Collections.emptyList(),
                          Collections.emptyList());
    }

    public List<DisplayLink> links() {
        return links;
    }

    public List<String> messages() {
        return texts;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Status) obj;
        return Objects.equals(this.links, that.links) &&
                Objects.equals(this.texts, that.texts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(links, texts);
    }

    @Override
    public String toString() {
        return "Status[" +
                "links=" + links + ", " +
                "texts=" + texts + ']';
    }

}
