package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.InMemoryHuddleRepository;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.ZonedDateTimeFactory;
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
        huddleService.changeNameDateTimeTo(huddleId, "After", afterZonedDateTime);

        Huddle found = huddleRepository.findById(huddleId).orElseThrow();
        assertThat(found.name())
                .isEqualTo("After");
        assertThat(found.startDateTime())
                .isEqualTo(afterZonedDateTime);
    }

}