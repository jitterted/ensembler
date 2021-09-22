package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.domain.Member;

public class MemberProfileForm {

    private String firstName;
    private String githubUsername;
    private String email;

    public MemberProfileForm() {
    }

    public MemberProfileForm(String firstName, String githubUsername, String email) {
        this.firstName = firstName;
        this.githubUsername = githubUsername;
        this.email = email;
    }

    public MemberProfileForm(Member member) {
        firstName = member.firstName();
        githubUsername = member.githubUsername();
        email = member.email();
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
}
