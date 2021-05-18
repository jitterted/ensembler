package com.jitterted.moborg.domain;

public class Participant {
    private final String name;
    private final String githubUsername;
    private final String email;
    private final String discordUsername;
    private final boolean newToMobbing;

    public Participant(String name, String githubUsername, String email, String discordUsername, boolean newToMobbing) {
        this.name = name;
        this.githubUsername = githubUsername;
        this.email = email;
        this.discordUsername = discordUsername;
        this.newToMobbing = newToMobbing;
    }

    public String name() {
        return name;
    }

    public String githubUsername() {
        return githubUsername;
    }

    public String email() {
        return email;
    }

    public String discordUsername() {
        return discordUsername;
    }

    public boolean isNewToMobbing() {
        return newToMobbing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Participant that = (Participant) o;

        return githubUsername.equals(that.githubUsername);
    }

    @Override
    public int hashCode() {
        return githubUsername.hashCode();
    }
}
