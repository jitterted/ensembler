package com.jitterted.moborg.domain;

import java.util.List;
import java.util.Optional;

public interface HuddleRepository {
  Huddle save(Huddle huddle);

  List<Huddle> findAll();

  Optional<Huddle> findById(HuddleId huddleId);
}
