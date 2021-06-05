package com.jitterted.mobreg.domain;

import java.util.List;
import java.util.Optional;

// strager suggests renaming this to HuddlePuddle
public interface HuddleRepository {
    Huddle save(Huddle huddle);

    List<Huddle> findAll();

    Optional<Huddle> findById(HuddleId huddleId);
}
