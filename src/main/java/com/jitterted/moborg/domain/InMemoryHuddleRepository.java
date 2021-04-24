package com.jitterted.moborg.domain;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHuddleRepository implements HuddleRepository {
  private final List<Huddle> huddles = new ArrayList<>();

  @Override
  public Huddle save(Huddle huddle) {
    huddles.add(huddle);
    return huddle;
  }

  @Override
  public List<Huddle> findAll() {
    return List.copyOf(huddles);
  }
}
