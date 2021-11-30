package com.jitterted.mobreg.application;

import com.jitterted.mobreg.domain.Ensemble;

import javax.validation.constraints.NotNull;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;

public class GoogleCalendarLinkCreator {

    private static final String GOOGLE_CALENDAR_LINK_BASE_URL = "https://calendar.google.com/calendar/render?action=TEMPLATE";
    private static final DateTimeFormatter GOOGLE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX");

    @NotNull
    public String createFor(Ensemble ensemble) {
        String huddleName = encodedNameOf(ensemble);
        String startDateTime = formattedStartDateTimeOf(ensemble);
        String endDateTime = formattedEndDateTimeOf(ensemble);
        String zoomDetails = encodedZoomLinkOf(ensemble);

        return "%s&text=%s&dates=%s/%s&details=%s"
                .formatted(
                        GOOGLE_CALENDAR_LINK_BASE_URL,
                        huddleName,
                        startDateTime,
                        endDateTime,
                        zoomDetails);
    }

    private String formattedEndDateTimeOf(Ensemble ensemble) {
        return ensemble.startDateTime().plusHours(2).format(GOOGLE_DATE_TIME_FORMATTER);
    }

    private String formattedStartDateTimeOf(Ensemble ensemble) {
        return ensemble.startDateTime().format(GOOGLE_DATE_TIME_FORMATTER);
    }

    private String encodedZoomLinkOf(Ensemble ensemble) {
        String zoomLinkString = ensemble.zoomMeetingLink().toString();
        return encode("Zoom link is: <a href='" + zoomLinkString + "'>" + zoomLinkString + "</a>");
    }

    private String encodedNameOf(Ensemble ensemble) {
        return encode(ensemble.name());
    }

    private String encode(String unencoded) {
        return URLEncoder.encode(unencoded, Charset.defaultCharset());
    }
}