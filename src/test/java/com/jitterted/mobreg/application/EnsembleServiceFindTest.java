package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class EnsembleServiceFindTest {

    @Test
    void whenRepositoryIsEmptyFindReturnsEmptyOptional() throws Exception {
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(new InMemoryEnsembleRepository());

        assertThat(ensembleService.findById(EnsembleId.of(9999)))
                .isEmpty();
    }

    @Test
    void whenRepositoryHasEnsembleFindByItsIdReturnsItInAnOptional() throws Exception {
        InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        Ensemble savedEnsemble = ensembleRepository.save(new Ensemble("test", ZonedDateTime.now()));
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository);

        Optional<Ensemble> foundEnsemble = ensembleService.findById(savedEnsemble.getId());

        assertThat(foundEnsemble)
                .isNotEmpty();
    }

    @Test
    void allEnsemblesOrderedByDateTimeDescendingIsInCorrectOrder() throws Exception {
        EnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository);
        ensembleService.scheduleEnsemble("two", ZonedDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault()));
        ensembleService.scheduleEnsemble("one", ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        ensembleService.scheduleEnsemble("three", ZonedDateTime.of(2021, 1, 3, 0, 0, 0, 0, ZoneId.systemDefault()));

        List<Ensemble> ensembles = ensembleService.allEnsemblesByDateTimeDescending();

        assertThat(ensembles)
                .extracting(Ensemble::name)
                .containsExactly("three", "two", "one");
    }

}
