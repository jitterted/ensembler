package com.jitterted.mobreg.application.port;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;

import java.util.List;
import java.util.Optional;

public interface HuddleRepository {
    Huddle save(Huddle huddle);

    List<Huddle> findAll();

    Optional<Huddle> findById(HuddleId huddleId);
}
