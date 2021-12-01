package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.domain.Member;

public class MemberProfileForm {

    private String firstName;
    private String githubUsername;
    private String email;
    private String timeZone;

    public MemberProfileForm() {
    }

    public MemberProfileForm(String firstName, String githubUsername, String email, String timeZone) {
        this.firstName = firstName;
        this.githubUsername = githubUsername;
        this.email = email;
        this.timeZone = timeZone;
    }

    public MemberProfileForm(Member member) {
        firstName = member.firstName();
        githubUsername = member.githubUsername();
        email = member.email();
        timeZone = member.timeZone().getId();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
