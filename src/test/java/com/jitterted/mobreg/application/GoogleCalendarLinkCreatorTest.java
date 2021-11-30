package com.jitterted.mobreg.application;

import com.jitterted.mobreg.domain.Ensemble;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class GoogleCalendarLinkCreatorTest {

    @Test
    public void googleCalendarLinkGeneratedFromHuddleWithZoomLinkInDetails() throws Exception {
        Ensemble calendarEnsemble = new Ensemble("Calendar Ensemble",
                                                 URI.create("https://zoom.us"),
                                                 ZonedDateTime.of(2021, 9, 17, 16, 0, 0, 0, ZoneOffset.UTC));

        String googleCalendarLink = new GoogleCalendarLinkCreator().createFor(calendarEnsemble);

        Assertions.assertThat(googleCalendarLink)
                  .isEqualTo("https://calendar.google.com/calendar/render?action=TEMPLATE"
                                     + "&text=Calendar+Ensemble"
                                     + "&dates=20210917T160000Z/20210917T180000Z"
                                     + "&details=Zoom+link+is%3A+%3Ca+href%3D%27https%3A%2F%2Fzoom.us%27%3Ehttps%3A%2F%2Fzoom.us%3C%2Fa%3E");
    }

}