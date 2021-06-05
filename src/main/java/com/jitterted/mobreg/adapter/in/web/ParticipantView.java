package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.Participant;

public record ParticipantView(String name,
                              String email,
                              String githubUsername,
                              String discordUsername,
                              boolean newToMobbing) {
    public static ParticipantView from(Participant participant) {
        return new ParticipantView(participant.name(),
                                   participant.email(),
                                   participant.githubUsername(),
                                   participant.discordUsername(),
                                   participant.isNewToMobbing());
    }
}
