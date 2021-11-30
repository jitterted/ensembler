package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.domain.Ensemble;

import java.util.List;

public record HuddleSummaryView(long id,
                                String name,
                                String zoomMeetingLink,
                                String dateTime,
                                boolean isCompleted,
                                int numberRegistered) {

    public static List<HuddleSummaryView> from(List<Ensemble> ensembles) {
        return ensembles.stream()
                        .map(HuddleSummaryView::toView)
                        .toList();
    }

    public static HuddleSummaryView toView(Ensemble ensemble) {
        return new HuddleSummaryView(ensemble.getId().id(),
                                     ensemble.name(),
                                     ensemble.zoomMeetingLink().toString(),
                                     DateTimeFormatting.formatAsDateTimeForJavaScriptDateIso8601(ensemble.startDateTime()),
                                     ensemble.isCompleted(),
                                     ensemble.acceptedCount());
    }
}
