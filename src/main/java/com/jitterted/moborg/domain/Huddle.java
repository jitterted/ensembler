package com.jitterted.moborg.domain;

import java.time.ZonedDateTime;

public class Huddle {
  private final String name;
  private final ZonedDateTime startDateTime;
  private int numberRegistered = 0;

  public Huddle(String name, ZonedDateTime startDateTime) {
    this.name = name;
    this.startDateTime = startDateTime;
  }

  public String name() {
    return name;
  }

  public ZonedDateTime startDateTime() {
    return startDateTime;
  }

  public int numberRegistered() {
    return numberRegistered;
  }
}
