package com.jitterted.moborg.adapter.in.web;

public class RegistrationForm {
    private String name;
    private String githubUsername;
    private long huddleId;

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

    public long getHuddleId() {
        return huddleId;
    }

    public void setHuddleId(long huddleId) {
        this.huddleId = huddleId;
    }
}
