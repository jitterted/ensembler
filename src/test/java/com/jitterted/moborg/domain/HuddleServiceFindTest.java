package com.jitterted.moborg.domain;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class HuddleServiceFindTest {

    @Test
    public void whenRepositoryIsEmptyFindReturnsEmptyOptional() throws Exception {
        HuddleService huddleService = new HuddleService(new InMemoryHuddleRepository());

        assertThat(huddleService.findById(HuddleId.of(9999)))
                .isEmpty();
    }

    @Test
    public void whenRepositoryHasHuddleFindByItsIdReturnsItInAnOptional() throws Exception {
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        Huddle savedHuddle = huddleRepository.save(new Huddle("test", ZonedDateTime.now()));
        HuddleService huddleService = new HuddleService(huddleRepository);

        Optional<Huddle> foundHuddle = huddleService.findById(savedHuddle.getId());

        assertThat(foundHuddle)
                .isNotEmpty();
    }
}
