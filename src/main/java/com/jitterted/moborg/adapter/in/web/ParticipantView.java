package com.jitterted.moborg.adapter.in.web;

public record ParticipantView(String fullName,
                              String email,
                              String gitHubUsername,
                              String discordUsername,
                              boolean newToMobbing) {
}
