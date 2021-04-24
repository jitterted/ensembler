package com.jitterted.moborg.domain;

import java.time.ZonedDateTime;
import java.util.List;

public class HuddleService {
  public List<Huddle> activeHuddles() {
    Huddle huddle = new Huddle("Name", ZonedDateTime.now());
    return List.of(huddle);
  }
}
