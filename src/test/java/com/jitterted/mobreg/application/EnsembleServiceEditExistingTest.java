package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.InMemoryHuddleRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.ZonedDateTimeFactory;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleServiceEditExistingTest {

    @Test
    public void changesToExistingHuddleAreSaved() throws Exception {
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(huddleRepository);
        Ensemble ensemble = new Ensemble("Before", URI.create("https://zoom.us/before"), ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 19, 17));
        EnsembleId ensembleId = huddleRepository.save(ensemble).getId();

        ZonedDateTime afterZonedDateTime = ZonedDateTimeFactory.zoneDateTimeUtc(2021, 11, 20, 18);
        huddleService.changeNameDateTimeTo(ensembleId, "After", afterZonedDateTime);

        Ensemble found = huddleRepository.findById(ensembleId).orElseThrow();
        assertThat(found.name())
                .isEqualTo("After");
        assertThat(found.startDateTime())
                .isEqualTo(afterZonedDateTime);
    }

}