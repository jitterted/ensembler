package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.domain.Huddle;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class GoogleCalendarLinkCreatorTest {

    @Test
    public void googleCalendarLinkGeneratedFromHuddleWithNoDetails() throws Exception {
        Huddle calendarEnsemble = new Huddle("Calendar Ensemble", ZonedDateTime.of(2021, 9, 17, 16, 0, 0, 0, ZoneOffset.UTC));

        String googleCalendarLink = new GoogleCalendarLinkCreator().createFor(calendarEnsemble);

        Assertions.assertThat(googleCalendarLink)
                  .isEqualTo("https://calendar.google.com/calendar/render?action=TEMPLATE"
                                     + "&text=Calendar+Ensemble"
                                     + "&dates=20210917T160000Z/20210917T180000Z");
    }


    //Test: WithZoomLinkInDetails

}