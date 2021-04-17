package com.jitterted.moborg.adapter.in.web;

public record Participant(String fullName,
                          String gitHubUsername,
                          String email,
                          String discordUsername,
                          boolean newToMobbing) {
}
