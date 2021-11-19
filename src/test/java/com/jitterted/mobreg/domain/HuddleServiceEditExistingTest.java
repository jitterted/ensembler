package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.InMemoryHuddleRepository;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleServiceEditExistingTest {

    @Test
    public void changesToExistingHuddleAreSaved() throws Exception {
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(huddleRepository);
        Huddle huddle = new Huddle("Before", URI.create("https://zoom.us/before"), ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 19, 17));
        HuddleId huddleId = huddleRepository.save(huddle).getId();

        ZonedDateTime afterZonedDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 20, 18);
        huddleService.changeDateTimeNameTo(huddleId, "After", afterZonedDateTime);

        Huddle found = huddleRepository.findById(huddleId).orElseThrow();
        assertThat(found.name())
                .isEqualTo("After");
        assertThat(found.startDateTime())
                .isEqualTo(afterZonedDateTime);
    }

}