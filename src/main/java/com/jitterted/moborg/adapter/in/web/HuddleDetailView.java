package com.jitterted.moborg.adapter.in.web;

public record HuddleDetailView(String name,
                               String startDateTime,
                               String duration,
                               String topic,
                               int size) {
}
