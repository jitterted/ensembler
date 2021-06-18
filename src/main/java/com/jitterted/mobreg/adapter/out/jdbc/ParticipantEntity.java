package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Member;

public class ParticipantEntity {
    private String firstName;
    private String githubUsername;

    public static ParticipantEntity from(Member member) {
        ParticipantEntity participantDto = new ParticipantEntity();
        participantDto.setFirstName(member.firstName());
        participantDto.setGithubUsername(member.githubUsername());
        return participantDto;
    }

    public Member asParticipant() {
        return new Member(firstName, githubUsername);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }
}
