package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.domain.Member;

import java.util.Objects;

public final class MemberView {
    private final Long id;
    private final String firstName;
    private final String githubUsername;
    private final String roles;

    MemberView(Long id,
               String firstName,
               String githubUsername,
               String roles) {
        this.id = id;
        this.firstName = firstName;
        this.githubUsername = githubUsername;
        this.roles = roles;
    }

    public Long id() {
        return id;
    }

    public String firstName() {
        return firstName;
    }

    public String githubUsername() {
        return githubUsername;
    }

    public String roles() {
        return roles;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MemberView) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.firstName, that.firstName) &&
                Objects.equals(this.githubUsername, that.githubUsername) &&
                Objects.equals(this.roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, githubUsername, roles);
    }

    @Override
    public String toString() {
        return "MemberView[" +
                "id=" + id + ", " +
                "firstName=" + firstName + ", " +
                "githubUsername=" + githubUsername + ", " +
                "roles=" + roles + ']';
    }

    public static MemberView from(Member member) {
        return new MemberView(member.getId().id(),
                              member.firstName(),
                              member.githubUsername(),
                              String.join(",", member.roles()));
    }
}
