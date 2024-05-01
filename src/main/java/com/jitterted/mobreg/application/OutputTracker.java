package com.jitterted.mobreg.application;

import java.util.ArrayList;
import java.util.List;

public class OutputTracker<T> {
  private final List<T> output = new ArrayList<>();
  private final OutputListener<T> outputListener;

  public OutputTracker(OutputListener<T> outputListener) {
    this.outputListener = outputListener;
  }

  void add(T data) {
    output.add(data);
  }

  public List<T> data() {
    return List.copyOf(output);
  }

  public List<T> clear() {
    List<T> data = this.data();
    output.clear();
    return data;
  }

  public void stop() {
    outputListener.remove(this);
  }
}