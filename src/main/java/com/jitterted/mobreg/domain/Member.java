package com.jitterted.mobreg.domain;

// This is the Aggregate Root for Member
public class Member {
    private MemberId id;

    private final String firstName;
    private final String githubUsername; // could be a Value Object

    public Member(String firstName, String githubUsername) {
        this.firstName = firstName;
        this.githubUsername = githubUsername;
    }

    public String firstName() {
        return firstName;
    }

    public String githubUsername() {
        return githubUsername;
    }

    public MemberId getId() {
        return id;
    }

    public void setId(MemberId id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member that = (Member) o;

        return githubUsername.equals(that.githubUsername);
    }

    @Override
    public int hashCode() {
        return githubUsername.hashCode();
    }
}
