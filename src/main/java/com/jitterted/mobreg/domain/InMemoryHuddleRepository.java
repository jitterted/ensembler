package com.jitterted.mobreg.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryHuddleRepository implements HuddleRepository {
    private final Map<HuddleId, Huddle> huddles = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Huddle save(Huddle huddle) {
        if (huddle.getId() == null) {
            huddle.setId(HuddleId.of(sequence.getAndIncrement()));
        }
        huddles.put(huddle.getId(), huddle);
        return huddle;
    }

    @Override
    public List<Huddle> findAll() {
        return List.copyOf(huddles.values());
    }

    @Override
    public Optional<Huddle> findById(HuddleId huddleId) {
        return Optional.ofNullable(huddles.get(huddleId));
    }
}
