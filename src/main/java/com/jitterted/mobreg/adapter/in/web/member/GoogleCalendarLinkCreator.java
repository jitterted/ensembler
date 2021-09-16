package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.domain.Huddle;

import javax.validation.constraints.NotNull;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;

public class GoogleCalendarLinkCreator {

    private static final String GOOGLE_CALENDAR_LINK_BASE_URL = "https://calendar.google.com/calendar/render?action=TEMPLATE";
    private static final DateTimeFormatter GOOGLE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX");

    @NotNull
    String createFor(Huddle huddle) {
        String huddleName = encodedNameOf(huddle);
        String startDateTime = formattedStartDateTimeOf(huddle);
        String endDateTime = formattedEndDateTimeOf(huddle);
        String zoomDetails = encodedZoomLinkOf(huddle);

        return "%s&text=%s&dates=%s/%s&details=%s"
                .formatted(
                        GOOGLE_CALENDAR_LINK_BASE_URL,
                        huddleName,
                        startDateTime,
                        endDateTime,
                        zoomDetails);
    }

    private String formattedEndDateTimeOf(Huddle huddle) {
        return huddle.startDateTime().plusHours(2).format(GOOGLE_DATE_TIME_FORMATTER);
    }

    private String formattedStartDateTimeOf(Huddle huddle) {
        return huddle.startDateTime().format(GOOGLE_DATE_TIME_FORMATTER);
    }

    private String encodedZoomLinkOf(Huddle huddle) {
        String zoomLinkString = huddle.zoomMeetingLink().toString();
        return encode("Zoom link is: <a href='" + zoomLinkString + "'>" + zoomLinkString + "</a>");
    }

    private String encodedNameOf(Huddle huddle) {
        return encode(huddle.name());
    }

    private String encode(String unencoded) {
        return URLEncoder.encode(unencoded, Charset.defaultCharset());
    }
}