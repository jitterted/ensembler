package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.domain.Ensemble;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleServiceScheduleTest {

    @Test
    public void singleScheduledEnsembleIsReturnedForAllEnsembles() throws Exception {
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(new InMemoryEnsembleRepository());

        ensembleService.scheduleEnsemble("Name", ZonedDateTime.now());

        assertThat(ensembleService.allEnsembles())
                .hasSize(1);
    }

    @Test
    public void ensembleScheduledWithZoomLinkThenHasZoomLink() throws Exception {
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(new InMemoryEnsembleRepository());

        ensembleService.scheduleEnsemble("With Zoom", URI.create("https://zoom.us/j/123456?pwd=12345"), ZonedDateTime.now());

        assertThat(ensembleService.allEnsembles())
                .extracting(Ensemble::zoomMeetingLink)
                .extracting(URI::toString)
                .containsOnly("https://zoom.us/j/123456?pwd=12345");
    }

}