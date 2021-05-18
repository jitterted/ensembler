package com.jitterted.moborg.adapter.out.file;

import java.util.List;

public class HuddlesDto {
  private List<HuddleDto> huddleDtos;

  public HuddlesDto() {
  }

  public HuddlesDto(List<HuddleDto> huddleDtos) {
    this.huddleDtos = huddleDtos;
  }

  public void setHuddleDtos(List<HuddleDto> huddleDtos) {
    this.huddleDtos = huddleDtos;
  }

  public List<HuddleDto> getHuddleDtos() {
    return huddleDtos;
  }
}
