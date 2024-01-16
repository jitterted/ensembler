package com.jitterted.mobreg.adapter.in.web.admin;

import jakarta.validation.constraints.NotBlank;

class AddMemberForm {
    @NotBlank
    private String firstName;
    @NotBlank
    private String githubUsername;

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
