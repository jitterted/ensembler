package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.InMemoryHuddleRepository;
import com.jitterted.mobreg.domain.Huddle;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleServiceScheduleTest {

    @Test
    public void singleScheduledHuddleIsReturnedForAllHuddles() throws Exception {
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(new InMemoryHuddleRepository());

        huddleService.scheduleHuddle("Name", ZonedDateTime.now());

        assertThat(huddleService.allHuddles())
                .hasSize(1);
    }

    @Test
    public void huddleScheduledWithZoomLinkThenHasZoomLink() throws Exception {
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(new InMemoryHuddleRepository());

        huddleService.scheduleHuddle("With Zoom", URI.create("https://zoom.us/j/123456?pwd=12345"), ZonedDateTime.now());

        assertThat(huddleService.allHuddles())
                .extracting(Huddle::zoomMeetingLink)
                .extracting(URI::toString)
                .containsOnly("https://zoom.us/j/123456?pwd=12345");
    }

}