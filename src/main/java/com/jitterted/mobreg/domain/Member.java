package com.jitterted.mobreg.domain;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

// This is the Aggregate Root for Member
public class Member {
    private MemberId id;

    private final String firstName;
    private final String githubUsername; // could be a Value Object
    private final Set<String> roles;
    private String email = "";

    public Member(String firstName, String githubUsername, String... roles) {
        this.firstName = firstName;
        this.githubUsername = githubUsername;
        this.roles = Arrays.stream(roles).collect(Collectors.toUnmodifiableSet());
    }

    public String firstName() {
        return firstName;
    }

    public String githubUsername() {
        return githubUsername;
    }

    public Set<String> roles() {
        return roles;
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

    public void changeEmailTo(String email) {
        this.email = email;
    }

    public String email() {
        return email;
    }
}
