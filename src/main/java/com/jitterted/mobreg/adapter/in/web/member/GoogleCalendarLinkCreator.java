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
        String huddleName = URLEncoder.encode(huddle.name(), Charset.defaultCharset());

        String startDateTime = huddle.startDateTime().format(GOOGLE_DATE_TIME_FORMATTER); // "20210917T160000Z"
        String endDateTime = huddle.startDateTime().plusHours(2).format(GOOGLE_DATE_TIME_FORMATTER); // "20210917T180000Z"
        String googleCalendarLink = GOOGLE_CALENDAR_LINK_BASE_URL + "&text=" + huddleName + "&dates=" + startDateTime + "/" + endDateTime;
        return googleCalendarLink;
    }
}