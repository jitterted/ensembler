package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.domain.EnsembleId;

public class AdminRegistrationForm {
    private String name;
    private String githubUsername;
    private String discordUsername;
    private long ensembleId;

    // used by Spring to create the incoming form
    public AdminRegistrationForm() {
    }

    // used by code to pre-populate ID
    public AdminRegistrationForm(EnsembleId ensembleId) {
        this.ensembleId = ensembleId.id();
    }

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

    public long getEnsembleId() {
        return ensembleId;
    }

    public void setEnsembleId(long ensembleId) {
        this.ensembleId = ensembleId;
    }

    public String getDiscordUsername() {
        return discordUsername;
    }

    public void setDiscordUsername(String discordUsername) {
        this.discordUsername = discordUsername;
    }
}
