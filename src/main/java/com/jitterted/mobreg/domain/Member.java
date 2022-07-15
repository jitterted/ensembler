package com.jitterted.mobreg.domain;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

// This is the Aggregate Root for Member
public class Member {
    private MemberId id;

    private String firstName;
    private final String githubUsername; // could be a Value Object
    private final Set<String> roles;
    private String email = "";
    private ZoneId timeZone = ZoneOffset.UTC;

    public Member(String firstName, String githubUsername, String... roles) {
        this.firstName = firstName;
        this.githubUsername = githubUsername;
        this.roles = Arrays.stream(roles).collect(Collectors.toUnmodifiableSet());
    }

    public Member(MemberState memberState) {
        this.id = memberState.memberId();
        this.firstName = memberState.firstName();
        this.githubUsername = memberState.githubUsername();
        this.roles = Set.copyOf(memberState.roles());
        this.email = memberState.email();
        this.timeZone = memberState.timeZone();
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

    public void changeEmailTo(String email) {
        this.email = email;
    }

    public String email() {
        return email;
    }

    public boolean hasEmail() {
        return !email().isEmpty();
    }

    public ZoneId timeZone() {
        return timeZone;
    }

    public void changeTimeZoneTo(ZoneId newTimeZone) {
        timeZone = newTimeZone;
    }

    public void changeFirstNameTo(String newFirstName) {
        firstName = newFirstName;
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

    @Override
    public String toString() {
        return "Member: " +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", githubUsername='" + githubUsername + '\'' +
                ", roles=" + roles +
                ", timeZone=" + timeZone;
    }

    public MemberState memento() {
        return new MemberState(firstName,
                               githubUsername,
                               roles,
                               email,
                               timeZone,
                               id);
    }
}
