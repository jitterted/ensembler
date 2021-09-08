package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.HuddleRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryHuddleRepository implements HuddleRepository {
    private final Map<HuddleId, Huddle> huddles = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);
    private int saveCount = 0;

    @Override
    public Huddle save(Huddle huddle) {
        if (huddle.getId() == null) {
            huddle.setId(HuddleId.of(sequence.getAndIncrement()));
        }
        huddles.put(huddle.getId(), huddle);
        saveCount++;
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

    public int saveCount() {
        return saveCount;
    }

    public void resetSaveCount() {
        saveCount = 0;
    }
}
