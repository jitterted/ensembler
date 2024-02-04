package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.domain.Ensemble;

import java.util.List;

public record EnsembleSummaryView(long id,
                                  String name,
                                  String zoomMeetingLink,
                                  String dateTime,
                                  String state,
                                  int numberAccepted,
                                  int numberDeclined) {

    public static List<EnsembleSummaryView> from(List<Ensemble> ensembles) {
        return ensembles.stream()
                        .map(EnsembleSummaryView::toView)
                        .toList();
    }

    public static EnsembleSummaryView toView(Ensemble ensemble) {
        return new EnsembleSummaryView(ensemble.getId().id(),
                                       ensemble.name(),
                                       ensemble.meetingLink().toString(),
                                       DateTimeFormatting.formatAsDateTimeForCommonIso8601(ensemble.startDateTime()),
                                       ensemble.state().toString().toLowerCase(),
                                       ensemble.participantCount(),
                                       ensemble.declinedCount());
    }
}
