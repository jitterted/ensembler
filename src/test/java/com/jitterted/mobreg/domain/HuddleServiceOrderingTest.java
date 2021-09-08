package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.HuddleRepository;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class HuddleServiceOrderingTest {

    @Test
    public void allHuddlesOrderedByDateTimeDescendingIsInCorrectOrder() throws Exception {
        HuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = new HuddleService(huddleRepository);
        huddleService.scheduleHuddle("two", ZonedDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault()));
        huddleService.scheduleHuddle("one", ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        huddleService.scheduleHuddle("three", ZonedDateTime.of(2021, 1, 3, 0, 0, 0, 0, ZoneId.systemDefault()));

        List<Huddle> huddles = huddleService.allHuddlesByDateTimeDescending();

        assertThat(huddles)
                .extracting(Huddle::name)
                .containsExactly("three", "two", "one");
    }

}