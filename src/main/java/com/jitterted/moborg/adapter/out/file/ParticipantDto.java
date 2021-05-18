package com.jitterted.moborg.adapter.out.file;

import com.jitterted.moborg.domain.Participant;

public class ParticipantDto {
    private String name;
    private String githubUsername;
    private String email;
    private String discordUsername;
    private boolean newToMobbing;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiscordUsername() {
        return discordUsername;
    }

    public void setDiscordUsername(String discordUsername) {
        this.discordUsername = discordUsername;
    }

    public boolean isNewToMobbing() {
        return newToMobbing;
    }

    public void setNewToMobbing(boolean newToMobbing) {
        this.newToMobbing = newToMobbing;
    }

    public static ParticipantDto from(Participant participant) {
        ParticipantDto participantDto = new ParticipantDto();
        participantDto.setDiscordUsername(participant.discordUsername());
        participantDto.setEmail(participant.email());
        participantDto.setName(participant.name());
        participantDto.setGithubUsername(participant.githubUsername());
        participantDto.setNewToMobbing(participant.isNewToMobbing());
        return participantDto;
    }

    public Participant asParticipant() {
        return new Participant(name, githubUsername, email, discordUsername, newToMobbing);
    }
}
